package org.prodacc.webapi.models

import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.FetchType
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime


@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "payment_id", nullable = false)
    val paymentId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    val invoice: Invoice,

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    val amount: java.math.BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    val paymentMethod: PaymentMethod,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,

    @Column(name = "transaction_reference")
    val transactionReference: String? = null,

    @Column(name = "payment_date")
    val paymentDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "reference_number")
    val referenceNumber: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "processed_by")
    val processedBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PaymentMethod {
    CASH, CARD, BANK_TRANSFER, MOBILE_MONEY, CHEQUE
}

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}
