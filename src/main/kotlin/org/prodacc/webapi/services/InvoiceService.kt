package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.*
import org.prodacc.webapi.repositories.*
import org.prodacc.webapi.services.dataTransferObjects.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val clientRepository: ClientRepository,
    private val jobCardRepository: JobCardRepository,
    private val quotationRepository: QuotationRepository,
    private val productRepository: ProductRepository,
    private val inventoryTransactionService: InventoryTransactionService,
    private val emailService: EmailService,
    private val pdfService: PdfGenerationService,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(InvoiceService::class.java)

    // ===== BASIC CRUD OPERATIONS =====

    fun getAllInvoices(pageable: Pageable): Page<InvoiceResponseDto> {
        logger.info("Fetching all invoices with pagination: ${pageable.pageNumber}, size: ${pageable.pageSize}")
        return invoiceRepository.findAll(pageable).map { it.toDto() }
    }

    fun getInvoiceById(id: UUID): InvoiceResponseDto {
        logger.info("Fetching invoice with ID: $id")
        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }
        return invoice.toDto()
    }

    fun getInvoiceWithDetails(id: UUID): InvoiceDetailedResponseDto {
        logger.info("Fetching detailed invoice with ID: $id")
        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }
        return invoice.toDetailedDto()
    }

    fun createInvoice(createDto: CreateInvoiceDto): InvoiceResponseDto {
        logger.info("Creating new invoice for client: ${createDto.clientId}")

        val client = clientRepository.findById(createDto.clientId)
            .orElseThrow { EntityNotFoundException("Client not found with ID: ${createDto.clientId}") }

        val jobCard = createDto.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Job card not found with ID: $it") }
        }

        val quotation = createDto.quotationId?.let {
            quotationRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Quotation not found with ID: $it") }
        }

        // Validate business rules
        validateInvoiceCreationRules(createDto, client, jobCard, quotation)

        val invoiceNumber = generateInvoiceNumber()

        val invoice = Invoice(
            invoiceNumber = invoiceNumber,
            jobCard = jobCard,
            quotation = quotation,
            client = client,
            dueDate = createDto.dueDate,
            taxRate = createDto.taxRate,
            discountPercentage = createDto.discountPercentage,
            paymentTerms = createDto.paymentTerms,
            notes = createDto.notes
        )

        val savedInvoice = invoiceRepository.save(invoice)

        // Create invoice items
        val items = createDto.items.map { itemDto ->
            createInvoiceItem(savedInvoice, itemDto)
        }

        invoiceItemRepository.saveAll(items)

        // Calculate totals and update invoice
        val updatedInvoice = savedInvoice.copy(items = items).calculateTotals()
        val finalInvoice = invoiceRepository.save(updatedInvoice)

        // Create inventory transactions for physical items
        processInventoryTransactions(finalInvoice)

        // Log audit trail
        auditService.logInvoiceCreated(finalInvoice)

        logger.info("Successfully created invoice with ID: ${finalInvoice.invoiceId}")
        return finalInvoice.toDto()
    }

    fun updateInvoice(id: UUID, updateDto: UpdateInvoiceDto): InvoiceResponseDto {
        logger.info("Updating invoice with ID: $id")

        val existingInvoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        // Validate that invoice can be updated
        if (!existingInvoice.status.isEditable()) {
            throw IllegalStateException("Cannot update invoice with status: ${existingInvoice.status}")
        }

        val updatedInvoice = existingInvoice.copy(
            dueDate = updateDto.dueDate ?: existingInvoice.dueDate,
            taxRate = updateDto.taxRate ?: existingInvoice.taxRate,
            discountPercentage = updateDto.discountPercentage ?: existingInvoice.discountPercentage,
            paymentTerms = updateDto.paymentTerms ?: existingInvoice.paymentTerms,
            notes = updateDto.notes ?: existingInvoice.notes,
            updatedAt = LocalDateTime.now()
        )

        // Update items if provided
        if (updateDto.items != null) {
            // Remove existing items
            invoiceItemRepository.deleteAllByInvoice(existingInvoice)

            // Create new items
            val newItems = updateDto.items.map { itemDto ->
                createInvoiceItem(updatedInvoice, itemDto)
            }
            invoiceItemRepository.saveAll(newItems)

            // Recalculate totals
            val recalculatedInvoice = updatedInvoice.copy(items = newItems).calculateTotals()
            val savedInvoice = invoiceRepository.save(recalculatedInvoice)

            auditService.logInvoiceUpdated(savedInvoice)
            return savedInvoice.toDto()
        }

        val savedInvoice = invoiceRepository.save(updatedInvoice)
        auditService.logInvoiceUpdated(savedInvoice)

        return savedInvoice.toDto()
    }

    fun deleteInvoice(id: UUID): String {
        logger.info("Deleting invoice with ID: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        if (!invoice.canBeCancelled()) {
            throw IllegalStateException("Cannot delete invoice with status: ${invoice.status}")
        }

        // Reverse inventory transactions
        reverseInventoryTransactions(invoice)

        // Soft delete by marking as cancelled
        val cancelledInvoice = invoice.copy(
            status = InvoiceStatus.CANCELLED,
            updatedAt = LocalDateTime.now()
        )
        invoiceRepository.save(cancelledInvoice)

        auditService.logInvoiceDeleted(invoice)

        return "Invoice successfully deleted"
    }

    // ===== STATUS MANAGEMENT =====

    fun updateInvoiceStatus(id: UUID, status: InvoiceStatus, notes: String?): InvoiceResponseDto {
        logger.info("Updating invoice status to $status for ID: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        validateStatusTransition(invoice.status, status)

        val updatedInvoice = invoice.copy(
            status = status,
            notes = if (notes != null) "${invoice.notes ?: ""}\n$notes" else invoice.notes,
            updatedAt = LocalDateTime.now()
        )

        val savedInvoice = invoiceRepository.save(updatedInvoice)
        auditService.logInvoiceStatusChanged(savedInvoice, invoice.status, status)

        return savedInvoice.toDto()
    }

    fun sendInvoice(id: UUID): InvoiceResponseDto {
        logger.info("Sending invoice with ID: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        if (invoice.status != InvoiceStatus.DRAFT) {
            throw IllegalStateException("Can only send draft invoices")
        }

        // Generate PDF and send email
        val pdfBytes = generateInvoicePdf(id)
        val emailRequest = EmailInvoiceRequest(
            emailAddress = invoice.client.email ?: throw IllegalStateException("Client has no email address"),
            subject = "Invoice ${invoice.invoiceNumber}",
            message = "Please find attached your invoice.",
            includePdf = true
        )

        emailService.sendInvoiceEmail(invoice, emailRequest, pdfBytes)

        // Update status to sent
        val sentInvoice = invoice.copy(
            status = InvoiceStatus.SENT,
            updatedAt = LocalDateTime.now()
        )

        val savedInvoice = invoiceRepository.save(sentInvoice)
        auditService.logInvoiceSent(savedInvoice)

        return savedInvoice.toDto()
    }

    fun cancelInvoice(id: UUID, reason: String?): InvoiceResponseDto {
        logger.info("Cancelling invoice with ID: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        if (!invoice.canBeCancelled()) {
            throw IllegalStateException("Cannot cancel invoice with status: ${invoice.status}")
        }

        // Reverse inventory transactions
        reverseInventoryTransactions(invoice)

        val cancelledInvoice = invoice.copy(
            status = InvoiceStatus.CANCELLED,
            notes = "${invoice.notes ?: ""}\nCancelled: ${reason ?: "No reason provided"}",
            updatedAt = LocalDateTime.now()
        )

        val savedInvoice = invoiceRepository.save(cancelledInvoice)
        auditService.logInvoiceCancelled(savedInvoice, reason)

        return savedInvoice.toDto()
    }

    // ===== FILTERING AND SEARCH =====

    fun getInvoicesByStatus(status: InvoiceStatus, pageable: Pageable): Page<InvoiceResponseDto> {
        logger.info("Fetching invoices with status: $status")
        return invoiceRepository.findByStatus(status, pageable).map { it.toDto() }
    }

    fun getOverdueInvoices(): List<InvoiceResponseDto> {
        logger.info("Fetching overdue invoices")
        return invoiceRepository.findOverdueInvoices().map { it.toDto() }
    }

    fun getInvoicesDueSoon(days: Int): List<InvoiceResponseDto> {
        logger.info("Fetching invoices due within $days days")
        val endDate = LocalDate.now().plusDays(days.toLong())
        return invoiceRepository.findInvoicesDueBetween(LocalDate.now(), endDate).map { it.toDto() }
    }

    fun getUnpaidInvoices(): List<InvoiceResponseDto> {
        logger.info("Fetching unpaid invoices")
        val unpaidStatuses = listOf(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)
        return invoiceRepository.findByStatusIn(unpaidStatuses).map { it.toDto() }
    }

    // ===== CLIENT-SPECIFIC OPERATIONS =====

    fun getClientInvoices(clientId: UUID, pageable: Pageable): Page<InvoiceResponseDto> {
        logger.info("Fetching invoices for client: $clientId")
        return invoiceRepository.findByClientOrderByDateDesc(clientId, pageable).map { it.toDto() }
    }

    fun getClientOutstandingInvoices(clientId: UUID): ClientOutstandingDto {
        logger.info("Fetching outstanding invoices for client: $clientId")

        val client = clientRepository.findById(clientId)
            .orElseThrow { EntityNotFoundException("Client not found with ID: $clientId") }

        val outstandingInvoices = invoiceRepository.findByClientAndStatusIn(
            client,
            listOf(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)
        )

        val totalOutstanding = outstandingInvoices.sumOf { it.balanceDue }
        val overdueAmount = outstandingInvoices.filter { it.isOverdue() }.sumOf { it.balanceDue }

        return ClientOutstandingDto(
            clientId = clientId,
            clientName = "${client.clientName} ${client.clientSurname}",
            totalOutstanding = totalOutstanding,
            overdueAmount = overdueAmount,
            invoiceCount = outstandingInvoices.size,
            invoices = outstandingInvoices.map { it.toDto() }
        )
    }

    // ===== JOB CARD INTEGRATION =====

    fun getInvoicesByJobCard(jobCardId: UUID): List<InvoiceResponseDto> {
        logger.info("Fetching invoices for job card: $jobCardId")
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job card not found with ID: $jobCardId") }

        return invoiceRepository.findByJobCard(jobCard).map { it.toDto() }
    }

    fun createInvoiceFromJobCard(jobCardId: UUID, additionalItems: List<CreateInvoiceItemDto>): InvoiceResponseDto {
        logger.info("Creating invoice from job card: $jobCardId")

        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job card not found with ID: $jobCardId") }

        // Check if job card already has an invoice
        val existingInvoices = invoiceRepository.findByJobCard(jobCard)
        if (existingInvoices.isNotEmpty()) {
            throw IllegalStateException("Job card already has invoices")
        }

        // Extract items from job card (this would depend on your job card structure)
        val jobCardItems = extractItemsFromJobCard(jobCard)
        val allItems = jobCardItems + additionalItems

        val createDto = CreateInvoiceDto(
            clientId = jobCard.customerReference?.id ?: throw IllegalStateException("Job card has no client"),
            jobCardId = jobCardId,
            dueDate = LocalDate.now().plusDays(30),
            items = allItems,
            notes = "Generated from Job Card #${jobCard.jobCardNumber}"
        )

        return createInvoice(createDto)
    }

    // ===== QUOTATION INTEGRATION =====

    fun createInvoiceFromQuotation(quotationId: UUID): InvoiceResponseDto {
        logger.info("Converting quotation to invoice: $quotationId")

        val quotation = quotationRepository.findById(quotationId)
            .orElseThrow { EntityNotFoundException("Quotation not found with ID: $quotationId") }

        if (quotation.status != QuotationStatus.ACCEPTED) {
            throw IllegalStateException("Can only convert accepted quotations to invoices")
        }

        if (quotation.convertedToJobCard) {
            throw IllegalStateException("Quotation has already been converted")
        }

        // Convert quotation items to invoice items
        val invoiceItems = quotation.items.map { quotationItem ->
            CreateInvoiceItemDto(
                productId = quotationItem.product?.productId,
                description = quotationItem.description,
                quantity = quotationItem.quantity,
                unitPrice = quotationItem.unitPrice,
                itemType = quotationItem.itemType
            )
        }

        val createDto = CreateInvoiceDto(
            clientId = quotation.client.id!!,
            quotationId = quotationId,
            dueDate = LocalDate.now().plusDays(30),
            taxRate = quotation.taxRate,
            discountPercentage = quotation.discountPercentage,
            items = invoiceItems,
            notes = "Generated from Quotation #${quotation.quotationNumber}"
        )

        val invoice = createInvoice(createDto)

        // Mark quotation as converted
        val updatedQuotation = quotation.copy(
            convertedToJobCard = true,
            updatedAt = LocalDateTime.now()
        )
        quotationRepository.save(updatedQuotation)

        return invoice
    }

    // ===== FINANCIAL REPORTING =====

    fun getRevenueReport(startDate: LocalDate, endDate: LocalDate): RevenueReportDto {
        logger.info("Generating revenue report from $startDate to $endDate")

        val totalRevenue = invoiceRepository.calculateRevenue(startDate, endDate) ?: BigDecimal.ZERO
        val invoiceCount = invoiceRepository.countInvoicesBetweenDates(startDate, endDate)
        val averageInvoiceValue = if (invoiceCount > 0) totalRevenue.divide(BigDecimal(invoiceCount), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO

        val revenueByStatus = InvoiceStatus.values().associateWith { status ->
            invoiceRepository.calculateRevenueByStatus(startDate, endDate, status) ?: BigDecimal.ZERO
        }

        return RevenueReportDto(
            startDate = startDate,
            endDate = endDate,
            totalRevenue = totalRevenue,
            invoiceCount = invoiceCount,
            averageInvoiceValue = averageInvoiceValue,
            revenueByStatus = revenueByStatus
        )
    }

    fun getOutstandingSummary(): OutstandingSummaryDto {
        logger.info("Fetching outstanding invoices summary")

        val totalOutstanding = invoiceRepository.calculateTotalOutstanding() ?: BigDecimal.ZERO
        val overdueAmount = invoiceRepository.calculateOverdueAmount() ?: BigDecimal.ZERO
        val currentAmount = totalOutstanding.subtract(overdueAmount)

        val outstandingInvoices = invoiceRepository.findByStatusIn(
            listOf(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)
        )

        val agingBuckets = calculateAgingBuckets(outstandingInvoices)

        return OutstandingSummaryDto(
            totalOutstanding = totalOutstanding,
            currentAmount = currentAmount,
            overdueAmount = overdueAmount,
            invoiceCount = outstandingInvoices.size,
            agingBuckets = agingBuckets
        )
    }

    // ===== DOCUMENT GENERATION =====

    fun generateInvoicePdf(id: UUID): ByteArray {
        logger.info("Generating PDF for invoice: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        return pdfService.generateInvoicePdf(invoice)
    }

    fun emailInvoice(id: UUID, emailRequest: EmailInvoiceRequest): EmailResult {
        logger.info("Emailing invoice $id to ${emailRequest.emailAddress}")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        val pdfBytes = if (emailRequest.includePdf) generateInvoicePdf(id) else null

        val result = emailService.sendInvoiceEmail(invoice, emailRequest, pdfBytes)

        auditService.logInvoiceEmailed(invoice, emailRequest.emailAddress)

        return result
    }

    // ===== SEARCH AND ANALYTICS =====

    fun searchInvoices(criteria: InvoiceSearchCriteria, pageable: Pageable): Page<InvoiceResponseDto> {
        logger.info("Searching invoices with criteria: $criteria")

        val specification = buildInvoiceSpecification(criteria)
        val invoices = invoiceRepository.findAll(specification, pageable)

        return invoices.map { it.toDto() }
    }

    fun getInvoiceAnalytics(startDate: LocalDate, endDate: LocalDate): InvoiceAnalyticsDto {
        logger.info("Fetching invoice analytics from $startDate to $endDate")

        val totalInvoices = invoiceRepository.countInvoicesBetweenDates(startDate, endDate)
        val totalRevenue = invoiceRepository.calculateRevenue(startDate, endDate) ?: BigDecimal.ZERO
        val averageInvoiceValue = if (totalInvoices > 0) totalRevenue.divide(BigDecimal(totalInvoices), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO

        val statusBreakdown = InvoiceStatus.values().associateWith { status ->
            invoiceRepository.countByStatusBetweenDates(startDate, endDate, status)
        }

        val monthlyRevenue = calculateMonthlyRevenue(startDate, endDate)
        val topClients = getTopClientsByRevenue(startDate, endDate, 10)

        return InvoiceAnalyticsDto(
            period = "$startDate to $endDate",
            totalInvoices = totalInvoices,
            totalRevenue = totalRevenue,
            averageInvoiceValue = averageInvoiceValue,
            statusBreakdown = statusBreakdown,
            monthlyRevenue = monthlyRevenue,
            topClients = topClients
        )
    }

    // ===== BULK OPERATIONS =====

    fun bulkSendInvoices(invoiceIds: List<UUID>): BulkOperationResult {
        logger.info("Bulk sending ${invoiceIds.size} invoices")

        val results = mutableListOf<BulkOperationItemResult>()
        var successCount = 0

        invoiceIds.forEach { id ->
            try {
                sendInvoice(id)
                results.add(BulkOperationItemResult(id, true, null))
                successCount++
            } catch (e: Exception) {
                results.add(BulkOperationItemResult(id, false, e.message))
            }
        }

        return BulkOperationResult(
            totalItems = invoiceIds.size,
            successCount = successCount,
            failureCount = invoiceIds.size - successCount,
            results = results
        )
    }

    fun bulkUpdateStatus(invoiceIds: List<UUID>, status: InvoiceStatus, notes: String?): BulkOperationResult {
        logger.info("Bulk updating status for ${invoiceIds.size} invoices to $status")

        val results = mutableListOf<BulkOperationItemResult>()
        var successCount = 0

        invoiceIds.forEach { id ->
            try {
                updateInvoiceStatus(id, status, notes)
                results.add(BulkOperationItemResult(id, true, null))
                successCount++
            } catch (e: Exception) {
                results.add(BulkOperationItemResult(id, false, e.message))
            }
        }

        return BulkOperationResult(
            totalItems = invoiceIds.size,
            successCount = successCount,
            failureCount = invoiceIds.size - successCount,
            results = results
        )
    }

    // ===== VALIDATION =====

    fun validateInvoice(id: UUID): InvoiceValidationResult {
        logger.info("Validating invoice with ID: $id")

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: $id") }

        val validationErrors = mutableListOf<String>()

        // Validate client
        if (invoice.client.email.isNullOrBlank()) {
            validationErrors.add("Client has no email address")
        }

        // Validate items
        if (invoice.items.isEmpty()) {
            validationErrors.add("Invoice has no items")
        }

        // Validate amounts
        if (invoice.totalAmount <= BigDecimal.ZERO) {
            validationErrors.add("Invoice total must be greater than zero")
        }

        // Validate calculations
        val calculatedInvoice = invoice.calculateTotals()
        if (calculatedInvoice.totalAmount != invoice.totalAmount) {
            validationErrors.add("Invoice calculations are incorrect")
        }

        return InvoiceValidationResult(
            isValid = validationErrors.isEmpty(),
            errors = validationErrors,
            warnings = emptyList() // Add warnings if needed
        )
    }

    fun validateInvoiceCreation(createDto: CreateInvoiceDto): ValidationResult {
        logger.info("Validating invoice creation request")

        val errors = mutableListOf<String>()

        // Validate client exists
        if (!clientRepository.existsById(createDto.clientId)) {
            errors.add("Client not found")
        }

        // Validate job card if provided
        createDto.jobCardId?.let { jobCardId ->
            if (!jobCardRepository.existsById(jobCardId)) {
                errors.add("Job card not found")
            }
        }

        // Validate items
        if (createDto.items.isEmpty()) {
            errors.add("Invoice must have at least one item")
        }

        createDto.items.forEachIndexed { index, item ->
            if (item.quantity <= BigDecimal.ZERO) {
                errors.add("Item ${index + 1}: Quantity must be greater than zero")
            }
            if (item.unitPrice < BigDecimal.ZERO) {
                errors.add("Item ${index + 1}: Unit price cannot be negative")
            }
            if (item.description.isBlank()) {
                errors.add("Item ${index + 1}: Description is required")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    // ===== PRIVATE HELPER METHODS =====

    private fun generateInvoiceNumber(): String {
        val today = LocalDate.now()
        val count = invoiceRepository.countTodaysInvoices()
        return "INV${today.year}${today.monthValue.toString().padStart(2, '0')}${today.dayOfMonth.toString().padStart(2, '0')}-${(count + 1).toString().padStart(4, '0')}"
    }

    private fun createInvoiceItem(invoice: Invoice, itemDto: CreateInvoiceItemDto): InvoiceItem {
        val product = itemDto.productId?.let {
            productRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Product not found with ID: $it") }
        }

        val totalPrice = itemDto.unitPrice.multiply(itemDto.quantity)

        return InvoiceItem(
            invoice = invoice,
            product = product,
            description = itemDto.description,
            quantity = itemDto.quantity,
            unitPrice = itemDto.unitPrice,
            totalPrice = totalPrice,
            itemType = itemDto.itemType
        )
    }

    private fun processInventoryTransactions(invoice: Invoice) {
        invoice.items.forEach { item ->
            if (item.product != null && item.itemType.requiresStockUpdate()) {
                val transactionDto = CreateInventoryTransactionDto(
                    productId = item.product.productId!!,
                    transactionType = TransactionType.SALE,
                    quantity = item.quantity.toInt(),
                    unitCost = item.product.costPrice,
                    referenceType = "INVOICE",
                    referenceId = invoice.invoiceId,
                    notes = "Sale via invoice ${invoice.invoiceNumber}"
                )
                inventoryTransactionService.createTransaction(transactionDto)
            }
        }
    }

    private fun reverseInventoryTransactions(invoice: Invoice) {
        invoice.items.forEach { item ->
            if (item.product != null && item.itemType.requiresStockUpdate()) {
                val transactionDto = CreateInventoryTransactionDto(
                    productId = item.product.productId!!,
                    transactionType = TransactionType.RETURN,
                    quantity = item.quantity.toInt(),
                    unitCost = item.product.costPrice,
                    referenceType = "INVOICE_REVERSAL",
                    referenceId = invoice.invoiceId,
                    notes = "Reversal of invoice ${invoice.invoiceNumber}"
                )
                inventoryTransactionService.createTransaction(transactionDto)
            }
        }
    }

    private fun validateInvoiceCreationRules(
        createDto: CreateInvoiceDto,
        client: Client,
        jobCard: JobCard?,
        quotation: Quotation?
    ) {
        // Add business validation rules here
        if (client.email.isNullOrBlank()) {
            // Log warning but don't fail - email might be added later
            logger.warn("Client ${client.id} has no email address for invoice delivery")
        }

        // Validate job card is not already invoiced
        if (jobCard != null) {
            val existingInvoices = invoiceRepository.findByJobCard(jobCard)
            if (existingInvoices.isNotEmpty()) {
                throw IllegalArgumentException("Job card ${jobCard.jobId} already has invoices")
            }
        }

        // Validate quotation is accepted if provided
        if (quotation != null && quotation.status != QuotationStatus.ACCEPTED) {
            throw IllegalArgumentException("Can only create invoices from accepted quotations")
        }
    }

    private fun validateStatusTransition(currentStatus: InvoiceStatus, newStatus: InvoiceStatus) {
        val validTransitions = mapOf(
            InvoiceStatus.DRAFT to listOf(InvoiceStatus.SENT, InvoiceStatus.CANCELLED),
            InvoiceStatus.SENT to listOf(InvoiceStatus.PAID, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE, InvoiceStatus.CANCELLED),
            InvoiceStatus.PARTIALLY_PAID to listOf(InvoiceStatus.PAID, InvoiceStatus.OVERDUE),
            InvoiceStatus.OVERDUE to listOf(InvoiceStatus.PAID, InvoiceStatus.PARTIALLY_PAID),
            InvoiceStatus.PAID to emptyList(),
            InvoiceStatus.CANCELLED to emptyList()
        )

        if (newStatus !in validTransitions[currentStatus].orEmpty()) {
            throw IllegalStateException("Cannot transition from $currentStatus to $newStatus")
        }
    }

    private fun extractItemsFromJobCard(jobCard: JobCard): List<CreateInvoiceItemDto> {
        // This would extract services and parts from the job card
        // Implementation depends on your job card structure
        return emptyList()
    }

    private fun buildInvoiceSpecification(criteria: InvoiceSearchCriteria): Specification<Invoice> {
        return Specification { root, query, criteriaBuilder ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            criteria.invoiceNumber?.let {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("invoiceNumber")),
                    "%${it.lowercase()}%"
                ))
            }

            criteria.clientName?.let {
                val clientJoin = root.join<Invoice, Client>("client")
                val clientNamePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(clientJoin.get("clientName")),
                        "%${it.lowercase()}%"
                    ),
                    criteriaBuilder.like(
                        criteriaBuilder.lower(clientJoin.get("clientSurname")),
                        "%${it.lowercase()}%"
                    )
                )
                predicates.add(clientNamePredicate)
            }

            criteria.minAmount?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), it))
            }

            criteria.maxAmount?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), it))
            }

            criteria.fromDate?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("invoiceDate"), it))
            }

            criteria.toDate?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("invoiceDate"), it))
            }

            criteria.status?.let {
                predicates.add(criteriaBuilder.equal(root.get<InvoiceStatus>("status"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun calculateAgingBuckets(invoices: List<Invoice>): Map<String, BigDecimal> {
        return mapOf(
            "current" to invoices.filter { !it.isOverdue() }
                .sumOf { it.balanceDue },
            "1-30 days" to invoices.filter {
                it.isOverdue() && (it.getDaysOverdue() ?: 0L) in 1..30
            }.sumOf { it.balanceDue },
            "31-60 days" to invoices.filter {
                it.isOverdue() && (it.getDaysOverdue() ?: 0L) in 31..60
            }.sumOf { it.balanceDue },
            "61-90 days" to invoices.filter {
                it.isOverdue() && (it.getDaysOverdue() ?: 0L) in 61..90
            }.sumOf { it.balanceDue },
            "90+ days" to invoices.filter {
                it.isOverdue() && (it.getDaysOverdue() ?: 0L) > 90
            }.sumOf { it.balanceDue }
        )
    }

    private fun calculateMonthlyRevenue(startDate: LocalDate, endDate: LocalDate): Map<String, BigDecimal> {
        val monthlyRevenue = mutableMapOf<String, BigDecimal>()
        var currentDate = startDate.withDayOfMonth(1)

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            val monthStart = currentDate
            val monthEnd = currentDate.plusMonths(1).minusDays(1)
            val revenue = invoiceRepository.calculateRevenue(monthStart, monthEnd) ?: BigDecimal.ZERO

            monthlyRevenue["${currentDate.year}-${currentDate.monthValue.toString().padStart(2, '0')}"] = revenue
            currentDate = currentDate.plusMonths(1)
        }

        return monthlyRevenue
    }

    private fun getTopClientsByRevenue(startDate: LocalDate, endDate: LocalDate, limit: Int): List<ClientRevenueDto> {
        return invoiceRepository.findTopClientsByRevenue(startDate, endDate, PageRequest.of(0, limit))
            .map { result ->
                val client = result[0] as Client
                val revenue = result[1] as BigDecimal
                ClientRevenueDto(
                    clientId = client.id!!,
                    clientName = "${client.clientName} ${client.clientSurname}",
                    revenue = revenue
                )
            }
    }
}