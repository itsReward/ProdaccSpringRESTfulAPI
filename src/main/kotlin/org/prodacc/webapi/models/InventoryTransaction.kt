package org.prodacc.webapi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "inventory_transactions")
data class InventoryTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "transaction_id", nullable = false)
    val transactionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType? = null,

    @Column(name = "quantity", nullable = false)
    val quantity: Int? = null,

    @Column(name = "unit_cost", precision = 12, scale = 2)
    val unitCost: java.math.BigDecimal? = null,

    @Column(name = "total_amount", precision = 12, scale = 2)
    val totalAmount: java.math.BigDecimal? = null,

    @Column(name = "reference_type")
    val referenceType: String? = null,

    @Column(name = "reference_id")
    val referenceId: UUID? = null,

    @Column(name = "transaction_date")
    val transactionDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    PURCHASE, SALE, ADJUSTMENT, RETURN, TRANSFER
}