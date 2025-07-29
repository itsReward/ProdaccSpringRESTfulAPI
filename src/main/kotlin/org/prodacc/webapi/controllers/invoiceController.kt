package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.prodacc.webapi.models.InvoiceStatus
import org.prodacc.webapi.services.InvoiceService
import org.prodacc.webapi.services.dataTransferObjects.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = ["*"])
class InvoiceController(
    private val invoiceService: InvoiceService
) {

    private val logger = LoggerFactory.getLogger(InvoiceController::class.java)

    // ===== BASIC CRUD OPERATIONS =====

    @GetMapping("/all")
    fun getAllInvoices(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDirection: String
    ): ResponseEntity<Page<InvoiceResponseDto>?> {
        logger.info("Fetching all invoices - Page: $page, Size: $size, Sort: $sortBy $sortDirection")

        return try {
            val direction = if (sortDirection.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
            val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
            val invoices = invoiceService.getAllInvoices(pageable)

            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching invoices: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/{id}")
    fun getInvoiceById(@PathVariable id: UUID): ResponseEntity<InvoiceResponseDto> {
        logger.info("Fetching invoice with ID: $id")

        return try {
            val invoice = invoiceService.getInvoiceById(id)
            ResponseEntity.ok(invoice)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Error fetching invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/{id}/detailed")
    fun getInvoiceWithDetails(@PathVariable id: UUID): ResponseEntity<InvoiceDetailedResponseDto> {
        logger.info("Fetching detailed invoice with ID: $id")

        return try {
            val invoice = invoiceService.getInvoiceWithDetails(id)
            ResponseEntity.ok(invoice)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Error fetching detailed invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/new")
    fun createInvoice(@Valid @RequestBody createDto: CreateInvoiceDto): ResponseEntity<Any> {
        logger.info("Creating new invoice for client: ${createDto.clientId}")

        return try {
            val invoice = invoiceService.createInvoice(createDto)
            logger.info("Successfully created invoice with ID: ${invoice.invoiceId}")
            ResponseEntity.status(HttpStatus.CREATED).body(invoice)
        } catch (e: EntityNotFoundException) {
            logger.error("Entity not found while creating invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid argument while creating invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error creating invoice: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    @PutMapping("/update/{id}")
    fun updateInvoice(
        @PathVariable id: UUID,
        @Valid @RequestBody updateDto: UpdateInvoiceDto
    ): ResponseEntity<Any> {
        logger.info("Updating invoice with ID: $id")

        return try {
            val invoice = invoiceService.updateInvoice(id, updateDto)
            ResponseEntity.ok(invoice)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for update with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid argument while updating invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error updating invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteInvoice(@PathVariable id: UUID): ResponseEntity<Any> {
        logger.info("Deleting invoice with ID: $id")

        return try {
            val result = invoiceService.deleteInvoice(id)
            ResponseEntity.ok(mapOf("message" to result))
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for deletion with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            logger.error("Cannot delete invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error deleting invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    // ===== STATUS MANAGEMENT =====

    @PutMapping("/{id}/status")
    fun updateInvoiceStatus(
        @PathVariable id: UUID,
        @RequestParam status: InvoiceStatus,
        @RequestParam(required = false) notes: String?
    ): ResponseEntity<Any> {
        logger.info("Updating invoice status to $status for ID: $id")

        return try {
            val invoice = invoiceService.updateInvoiceStatus(id, status, notes)
            ResponseEntity.ok(invoice)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for status update with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            logger.error("Invalid status transition: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error updating invoice status $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    @PostMapping("/{id}/send")
    fun sendInvoice(@PathVariable id: UUID): ResponseEntity<Any> {
        logger.info("Sending invoice with ID: $id")

        return try {
            val result = invoiceService.sendInvoice(id)
            ResponseEntity.ok(mapOf(
                "message" to "Invoice sent successfully",
                "invoice" to result
            ))
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for sending with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            logger.error("Cannot send invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error sending invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    @PostMapping("/{id}/cancel")
    fun cancelInvoice(
        @PathVariable id: UUID,
        @RequestParam(required = false) reason: String?
    ): ResponseEntity<Any> {
        logger.info("Cancelling invoice with ID: $id")

        return try {
            val invoice = invoiceService.cancelInvoice(id, reason)
            ResponseEntity.ok(invoice)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for cancellation with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            logger.error("Cannot cancel invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error cancelling invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    // ===== FILTERING AND SEARCH =====

    @GetMapping("/filter")
    fun getInvoicesByStatus(
        @RequestParam status: InvoiceStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<InvoiceResponseDto>> {
        logger.info("Fetching invoices with status: $status")

        return try {
            val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
            val invoices = invoiceService.getInvoicesByStatus(status, pageable)
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching invoices by status $status: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/overdue")
    fun getOverdueInvoices(): ResponseEntity<List<InvoiceResponseDto>> {
        logger.info("Fetching overdue invoices")

        return try {
            val invoices = invoiceService.getOverdueInvoices()
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching overdue invoices: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/due-soon")
    fun getInvoicesDueSoon(
        @RequestParam(defaultValue = "7") days: Int
    ): ResponseEntity<List<InvoiceResponseDto>> {
        logger.info("Fetching invoices due within $days days")

        return try {
            val invoices = invoiceService.getInvoicesDueSoon(days)
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching invoices due soon: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/unpaid")
    fun getUnpaidInvoices(): ResponseEntity<List<InvoiceResponseDto>> {
        logger.info("Fetching unpaid invoices")

        return try {
            val invoices = invoiceService.getUnpaidInvoices()
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching unpaid invoices: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    // ===== CLIENT-SPECIFIC OPERATIONS =====

    @GetMapping("/client/{clientId}")
    fun getClientInvoices(
        @PathVariable clientId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<InvoiceResponseDto>> {
        logger.info("Fetching invoices for client: $clientId")

        return try {
            val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"))
            val invoices = invoiceService.getClientInvoices(clientId, pageable)
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching client invoices for $clientId: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/client/{clientId}/outstanding")
    fun getClientOutstandingInvoices(@PathVariable clientId: UUID): ResponseEntity<Any> {
        logger.info("Fetching outstanding invoices for client: $clientId")

        return try {
            val result = invoiceService.getClientOutstandingInvoices(clientId)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            logger.error("Error fetching client outstanding invoices for $clientId: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    // ===== JOB CARD INTEGRATION =====

    @GetMapping("/job-card/{jobCardId}")
    fun getInvoicesByJobCard(@PathVariable jobCardId: UUID): ResponseEntity<List<InvoiceResponseDto>> {
        logger.info("Fetching invoices for job card: $jobCardId")

        return try {
            val invoices = invoiceService.getInvoicesByJobCard(jobCardId)
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error fetching invoices for job card $jobCardId: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/job-card/{jobCardId}/create")
    fun createInvoiceFromJobCard(
        @PathVariable jobCardId: UUID,
        @RequestBody(required = false) additionalItems: List<CreateInvoiceItemDto>?
    ): ResponseEntity<Any> {
        logger.info("Creating invoice from job card: $jobCardId")

        return try {
            val invoice = invoiceService.createInvoiceFromJobCard(jobCardId, additionalItems ?: emptyList())
            ResponseEntity.status(HttpStatus.CREATED).body(invoice)
        } catch (e: EntityNotFoundException) {
            logger.error("Job card not found: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: IllegalStateException) {
            logger.error("Cannot create invoice from job card: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error creating invoice from job card $jobCardId: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    // ===== QUOTATION INTEGRATION =====

    @PostMapping("/quotation/{quotationId}/convert")
    fun createInvoiceFromQuotation(@PathVariable quotationId: UUID): ResponseEntity<Any> {
        logger.info("Converting quotation to invoice: $quotationId")

        return try {
            val invoice = invoiceService.createInvoiceFromQuotation(quotationId)
            ResponseEntity.status(HttpStatus.CREATED).body(invoice)
        } catch (e: EntityNotFoundException) {
            logger.error("Quotation not found: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: IllegalStateException) {
            logger.error("Cannot convert quotation to invoice: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Error converting quotation $quotationId to invoice: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }

    // ===== FINANCIAL REPORTING =====

    @GetMapping("/revenue")
    fun getRevenueReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<RevenueReportDto> {
        logger.info("Generating revenue report from $startDate to $endDate")

        return try {
            val report = invoiceService.getRevenueReport(startDate, endDate)
            ResponseEntity.ok(report)
        } catch (e: Exception) {
            logger.error("Error generating revenue report: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/outstanding-summary")
    fun getOutstandingSummary(): ResponseEntity<OutstandingSummaryDto> {
        logger.info("Fetching outstanding invoices summary")

        return try {
            val summary = invoiceService.getOutstandingSummary()
            ResponseEntity.ok(summary)
        } catch (e: Exception) {
            logger.error("Error fetching outstanding summary: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    // ===== DOCUMENT GENERATION =====

    @GetMapping("/{id}/pdf")
    fun generateInvoicePdf(@PathVariable id: UUID): ResponseEntity<ByteArray> {
        logger.info("Generating PDF for invoice: $id")

        return try {
            val pdfBytes = invoiceService.generateInvoicePdf(id)
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_PDF
                setContentDispositionFormData("attachment", "invoice-$id.pdf")
                contentLength = pdfBytes.size.toLong()
            }

            ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for PDF generation with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Error generating PDF for invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/{id}/email")
    fun emailInvoice(
        @PathVariable id: UUID,
        @RequestBody emailRequest: EmailInvoiceRequest
    ): ResponseEntity<Any> {
        logger.info("Emailing invoice $id to ${emailRequest.emailAddress}")

        return try {
            val result = invoiceService.emailInvoice(id, emailRequest)
            ResponseEntity.ok(mapOf(
                "message" to "Invoice email sent successfully",
                "result" to result
            ))
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for emailing with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Error emailing invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Failed to send email"))
        }
    }

    // ===== SEARCH AND ANALYTICS =====

    @GetMapping("/search")
    fun searchInvoices(
        @RequestParam(required = false) invoiceNumber: String?,
        @RequestParam(required = false) clientName: String?,
        @RequestParam(required = false) minAmount: BigDecimal?,
        @RequestParam(required = false) maxAmount: BigDecimal?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate?,
        @RequestParam(required = false) status: InvoiceStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<InvoiceResponseDto>> {
        logger.info("Searching invoices with criteria")

        return try {
            val searchCriteria = InvoiceSearchCriteria(
                invoiceNumber = invoiceNumber,
                clientName = clientName,
                minAmount = minAmount,
                maxAmount = maxAmount,
                fromDate = fromDate,
                toDate = toDate,
                status = status
            )

            val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
            val invoices = invoiceService.searchInvoices(searchCriteria, pageable)
            ResponseEntity.ok(invoices)
        } catch (e: Exception) {
            logger.error("Error searching invoices: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/analytics")
    fun getInvoiceAnalytics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<InvoiceAnalyticsDto> {
        logger.info("Fetching invoice analytics")

        return try {
            val analytics = invoiceService.getInvoiceAnalytics(
                startDate ?: LocalDate.now().minusMonths(12),
                endDate ?: LocalDate.now()
            )
            ResponseEntity.ok(analytics)
        } catch (e: Exception) {
            logger.error("Error fetching invoice analytics: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    // ===== BULK OPERATIONS =====

    @PostMapping("/bulk/send")
    fun bulkSendInvoices(@RequestBody invoiceIds: List<UUID>): ResponseEntity<Any> {
        logger.info("Bulk sending ${invoiceIds.size} invoices")

        return try {
            val result = invoiceService.bulkSendInvoices(invoiceIds)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            logger.error("Error bulk sending invoices: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Bulk send operation failed"))
        }
    }

    @PostMapping("/bulk/update-status")
    fun bulkUpdateInvoiceStatus(
        @RequestBody request: BulkStatusUpdateRequest
    ): ResponseEntity<Any> {
        logger.info("Bulk updating status for ${request.invoiceIds.size} invoices to ${request.status}")

        return try {
            val result = invoiceService.bulkUpdateStatus(request.invoiceIds, request.status, request.notes)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            logger.error("Error bulk updating invoice status: ${e.message}", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "Bulk update operation failed"))
        }
    }

    // ===== VALIDATION ENDPOINTS =====

    @GetMapping("/{id}/validate")
    fun validateInvoice(@PathVariable id: UUID): ResponseEntity<InvoiceValidationResult> {
        logger.info("Validating invoice with ID: $id")

        return try {
            val validation = invoiceService.validateInvoice(id)
            ResponseEntity.ok(validation)
        } catch (e: EntityNotFoundException) {
            logger.warn("Invoice not found for validation with ID: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Error validating invoice $id: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/validate-creation")
    fun validateInvoiceCreation(@RequestBody createDto: CreateInvoiceDto): ResponseEntity<ValidationResult> {
        logger.info("Validating invoice creation request")

        return try {
            val validation = invoiceService.validateInvoiceCreation(createDto)
            ResponseEntity.ok(validation)
        } catch (e: Exception) {
            logger.error("Error validating invoice creation: ${e.message}", e)
            ResponseEntity.badRequest().body(ValidationResult(false, listOf(e.message ?: "Validation failed")))
        }
    }
}