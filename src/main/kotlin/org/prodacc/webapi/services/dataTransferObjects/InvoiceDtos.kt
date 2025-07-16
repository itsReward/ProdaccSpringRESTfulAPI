package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.InvoiceItem
import org.prodacc.webapi.models.InvoiceStatus
import org.prodacc.webapi.models.ItemType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class CreateInvoiceDto(
    val jobCardId: UUID? = null,
    val quotationId: UUID? = null,
    val clientId: UUID,
    val dueDate: LocalDate? = null,
    val taxRate: BigDecimal = BigDecimal("15.00"),
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val paymentTerms: String? = null,
    val notes: String? = null,
    val items: List<CreateInvoiceItemDto> = emptyList()
)

data class CreateInvoiceItemDto(
    val productId: UUID? = null,
    val description: String,
    val quantity: BigDecimal = BigDecimal.ONE,
    val unitPrice: BigDecimal,
    val itemType: ItemType
)

data class InvoiceResponseDto(
    val invoiceId: UUID,
    val invoiceNumber: String,
    val clientName: String,
    val clientSurname: String,
    val jobCardNumber: Int?,
    val invoiceDate: LocalDate,
    val dueDate: LocalDate?,
    val subtotal: BigDecimal,
    val taxRate: BigDecimal,
    val taxAmount: BigDecimal,
    val discountPercentage: BigDecimal,
    val discountAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val amountPaid: BigDecimal,
    val balanceDue: BigDecimal,
    val status: InvoiceStatus,
    val paymentTerms: String?,
    val notes: String?,
    val createdAt: LocalDateTime,
    val items: List<InvoiceItemResponseDto> = emptyList(),
    val payments: List<PaymentResponseDto> = emptyList()
)

data class InvoiceItemResponseDto(
    val itemId: UUID,
    val productCode: String?,
    val description: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val itemType: ItemType
)


fun Invoice.toDto(): InvoiceResponseDto = InvoiceResponseDto(
    invoiceId = this.invoiceId!!,
    invoiceNumber = this.invoiceNumber,
    clientName = this.client.clientName ?: "",
    clientSurname = this.client.clientSurname ?: "",
    jobCardNumber = this.jobCard?.jobCardNumber,
    invoiceDate = this.invoiceDate,
    dueDate = this.dueDate,
    subtotal = this.subtotal,
    taxRate = this.taxRate,
    taxAmount = this.taxAmount,
    discountPercentage = this.discountPercentage,
    discountAmount = this.discountAmount,
    totalAmount = this.totalAmount,
    amountPaid = this.amountPaid,
    balanceDue = this.balanceDue,
    status = this.status,
    paymentTerms = this.paymentTerms,
    notes = this.notes,
    createdAt = this.createdAt,
    items = this.items.map { it.toDto() },
    payments = this.payments.map { it.toDto() }
)

fun InvoiceItem.toDto(): InvoiceItemResponseDto = InvoiceItemResponseDto(
    itemId = this.itemId!!,
    productCode = this.product?.productCode,
    description = this.description,
    quantity = this.quantity,
    unitPrice = this.unitPrice,
    totalPrice = this.totalPrice,
    itemType = this.itemType
)


data class UpdateInvoiceDto(
    val dueDate: LocalDate? = null,
    val taxRate: BigDecimal? = null,
    val discountPercentage: BigDecimal? = null,
    val paymentTerms: String? = null,
    val notes: String? = null,
    val items: List<CreateInvoiceItemDto>? = null
)

data class InvoiceDetailedResponseDto(
    val invoiceId: UUID,
    val invoiceNumber: String,
    val client: ClientSummaryDto,
    val jobCard: JobCardSummaryDto?,
    val quotation: QuotationSummaryDto?,
    val invoiceDate: LocalDate,
    val dueDate: LocalDate?,
    val subtotal: BigDecimal,
    val taxRate: BigDecimal,
    val taxAmount: BigDecimal,
    val discountPercentage: BigDecimal,
    val discountAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val amountPaid: BigDecimal,
    val balanceDue: BigDecimal,
    val status: InvoiceStatus,
    val paymentTerms: String?,
    val notes: String?,
    val items: List<InvoiceItemResponseDto>,
    val payments: List<PaymentResponseDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isOverdue: Boolean,
    val daysOverdue: Long?
)

data class ClientOutstandingDto(
    val clientId: UUID,
    val clientName: String,
    val totalOutstanding: BigDecimal,
    val overdueAmount: BigDecimal,
    val invoiceCount: Int,
    val invoices: List<InvoiceResponseDto>
)

data class RevenueReportDto(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalRevenue: BigDecimal,
    val invoiceCount: Long,
    val averageInvoiceValue: BigDecimal,
    val revenueByStatus: Map<InvoiceStatus, BigDecimal>
)

data class OutstandingSummaryDto(
    val totalOutstanding: BigDecimal,
    val currentAmount: BigDecimal,
    val overdueAmount: BigDecimal,
    val invoiceCount: Int,
    val agingBuckets: Map<String, BigDecimal>
)

data class InvoiceSearchCriteria(
    val invoiceNumber: String? = null,
    val clientName: String? = null,
    val minAmount: BigDecimal? = null,
    val maxAmount: BigDecimal? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val status: InvoiceStatus? = null
)

data class InvoiceAnalyticsDto(
    val period: String,
    val totalInvoices: Long,
    val totalRevenue: BigDecimal,
    val averageInvoiceValue: BigDecimal,
    val statusBreakdown: Map<InvoiceStatus, Long>,
    val monthlyRevenue: Map<String, BigDecimal>,
    val topClients: List<ClientRevenueDto>
)

data class EmailInvoiceRequest(
    val emailAddress: String,
    val subject: String,
    val message: String,
    val includePdf: Boolean = true
)

data class EmailResult(
    val success: Boolean,
    val message: String,
    val emailId: String? = null
)

data class BulkOperationResult(
    val totalItems: Int,
    val successCount: Int,
    val failureCount: Int,
    val results: List<BulkOperationItemResult>
)

data class BulkOperationItemResult(
    val itemId: UUID,
    val success: Boolean,
    val errorMessage: String?
)

data class BulkStatusUpdateRequest(
    val invoiceIds: List<UUID>,
    val status: InvoiceStatus,
    val notes: String? = null
)

data class InvoiceValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

data class ClientSummaryDto(
    val clientId: UUID,
    val clientName: String,
    val clientSurname: String,
    val email: String?,
    val phone: String?
)

data class JobCardSummaryDto(
    val jobCardId: UUID,
    val jobCardNumber: Int,
    val jobCardName: String
)

data class QuotationSummaryDto(
    val quotationId: UUID,
    val quotationNumber: String,
    val totalAmount: BigDecimal
)

data class ClientRevenueDto(
    val clientId: UUID,
    val clientName: String,
    val revenue: BigDecimal
)

// ===== EXTENSION FUNCTIONS =====

fun Invoice.toDetailedDto(): InvoiceDetailedResponseDto = InvoiceDetailedResponseDto(
    invoiceId = this.invoiceId!!,
    invoiceNumber = this.invoiceNumber,
    client = ClientSummaryDto(
        clientId = this.client.id!!,
        clientName = this.client.clientName ?: "",
        clientSurname = this.client.clientSurname ?: "",
        email = this.client.email,
        phone = this.client.phone
    ),
    jobCard = this.jobCard?.let { JobCardSummaryDto(
        jobCardId = it.jobId!!,
        jobCardNumber = it.jobCardNumber ?: 0,
        jobCardName = it.jobCardName ?: ""
    ) },
    quotation = this.quotation?.let { QuotationSummaryDto(
        quotationId = it.quotationId!!,
        quotationNumber = it.quotationNumber,
        totalAmount = it.totalAmount
    ) },
    invoiceDate = this.invoiceDate,
    dueDate = this.dueDate,
    subtotal = this.subtotal,
    taxRate = this.taxRate,
    taxAmount = this.taxAmount,
    discountPercentage = this.discountPercentage,
    discountAmount = this.discountAmount,
    totalAmount = this.totalAmount,
    amountPaid = this.amountPaid,
    balanceDue = this.balanceDue,
    status = this.status,
    paymentTerms = this.paymentTerms,
    notes = this.notes,
    items = this.items.map { it.toDto() },
    payments = this.payments.map { it.toDto() },
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    isOverdue = this.isOverdue(),
    daysOverdue = this.getDaysOverdue()
)