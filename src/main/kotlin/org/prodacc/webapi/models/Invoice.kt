package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "invoices")
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "invoice_id", nullable = false)
    val invoiceId: UUID? = null,

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    val invoiceNumber: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    val jobCard: JobCard? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    val quotation: Quotation? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val client: Client,

    @Column(name = "invoice_date", nullable = false)
    val invoiceDate: LocalDate = LocalDate.now(),

    @Column(name = "due_date")
    val dueDate: LocalDate? = null,

    @Column(name = "subtotal", precision = 12, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "tax_rate", precision = 5, scale = 2)
    val taxRate: BigDecimal = BigDecimal("15.00"),

    @Column(name = "tax_amount", precision = 12, scale = 2)
    var taxAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    val discountPercentage: BigDecimal = BigDecimal.ZERO,

    @Column(name = "discount_amount", precision = 12, scale = 2)
    var discountAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_amount", precision = 12, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "amount_paid", precision = 12, scale = 2)
    var amountPaid: BigDecimal = BigDecimal.ZERO,

    @Column(name = "balance_due", precision = 12, scale = 2)
    var balanceDue: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    var status: InvoiceStatus = InvoiceStatus.DRAFT,

    @Column(name = "payment_terms", length = 100)
    val paymentTerms: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    // Relationships
    @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: List<InvoiceItem> = emptyList(),

    @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val payments: List<Payment> = emptyList(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {

    // Business logic methods
    fun calculateTotals(): Invoice {
        val itemsSubtotal = items.sumOf { it.totalPrice }
        val calculatedDiscountAmount = itemsSubtotal.multiply(discountPercentage).divide(BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
        val taxableAmount = itemsSubtotal.subtract(calculatedDiscountAmount)
        val calculatedTaxAmount = taxableAmount.multiply(taxRate).divide(BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
        val calculatedTotalAmount = taxableAmount.add(calculatedTaxAmount)
        val calculatedBalanceDue = calculatedTotalAmount.subtract(amountPaid)

        return this.copy(
            subtotal = itemsSubtotal,
            discountAmount = calculatedDiscountAmount,
            taxAmount = calculatedTaxAmount,
            totalAmount = calculatedTotalAmount,
            balanceDue = calculatedBalanceDue,
            updatedAt = LocalDateTime.now()
        )
    }

    fun updatePaymentStatus(paymentAmount: BigDecimal): Invoice {
        val newAmountPaid = amountPaid.add(paymentAmount)
        val newBalanceDue = totalAmount.subtract(newAmountPaid)

        val newStatus = when {
            newBalanceDue <= BigDecimal.ZERO -> InvoiceStatus.PAID
            newAmountPaid > BigDecimal.ZERO -> InvoiceStatus.PARTIALLY_PAID
            else -> status
        }

        return this.copy(
            amountPaid = newAmountPaid,
            balanceDue = newBalanceDue,
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )
    }

    fun isOverdue(): Boolean {
        return dueDate != null &&
                dueDate.isBefore(LocalDate.now()) &&
                status in listOf(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID)
    }

    fun isPaid(): Boolean = status == InvoiceStatus.PAID

    fun isPartiallyPaid(): Boolean = status == InvoiceStatus.PARTIALLY_PAID

    fun canBeCancelled(): Boolean = status in listOf(InvoiceStatus.DRAFT, InvoiceStatus.SENT)

    fun getPaymentStatusDescription(): String {
        return when (status) {
            InvoiceStatus.DRAFT -> "Draft - Not yet sent"
            InvoiceStatus.SENT -> "Sent - Awaiting payment"
            InvoiceStatus.PARTIALLY_PAID -> "Partially Paid - ${amountPaid} of ${totalAmount}"
            InvoiceStatus.PAID -> "Fully Paid"
            InvoiceStatus.OVERDUE -> "Overdue - Payment required"
            InvoiceStatus.CANCELLED -> "Cancelled"
        }
    }

    fun getDaysOverdue(): Long? {
        return if (isOverdue()) {
            dueDate?.let {
                java.time.temporal.ChronoUnit.DAYS.between(it, LocalDate.now())
            }
        } else null
    }

    fun getFormattedInvoiceNumber(): String = invoiceNumber

    fun getClientFullName(): String = "${client.clientName} ${client.clientSurname}"

    fun getJobCardNumber(): String? = jobCard?.jobCardNumber?.toString()

    // Equals and hashCode based on invoiceId
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Invoice) return false
        return invoiceId == other.invoiceId
    }

    override fun hashCode(): Int {
        return invoiceId?.hashCode() ?: 0
    }

    // ToString method for debugging
    override fun toString(): String {
        return "Invoice(invoiceId=$invoiceId, invoiceNumber='$invoiceNumber', " +
                "clientName='${getClientFullName()}', totalAmount=$totalAmount, " +
                "status=$status, createdAt=$createdAt)"
    }
}

// Invoice Status Enum
enum class InvoiceStatus {
    DRAFT,
    SENT,
    PAID,
    PARTIALLY_PAID,
    OVERDUE,
    CANCELLED;

    fun getDisplayName(): String {
        return when (this) {
            DRAFT -> "Draft"
            SENT -> "Sent"
            PAID -> "Paid"
            PARTIALLY_PAID -> "Partially Paid"
            OVERDUE -> "Overdue"
            CANCELLED -> "Cancelled"
        }
    }

    fun getColor(): String {
        return when (this) {
            DRAFT -> "#6B7280"      // Gray
            SENT -> "#3B82F6"       // Blue
            PAID -> "#10B981"       // Green
            PARTIALLY_PAID -> "#F59E0B"  // Amber
            OVERDUE -> "#EF4444"    // Red
            CANCELLED -> "#6B7280"  // Gray
        }
    }

    fun isPayable(): Boolean {
        return this in listOf(SENT, PARTIALLY_PAID, OVERDUE)
    }

    fun isEditable(): Boolean {
        return this == DRAFT
    }
}