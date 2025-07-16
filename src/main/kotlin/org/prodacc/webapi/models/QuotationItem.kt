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
@Table(name = "quotation_items")
data class QuotationItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "item_id", nullable = false)
    val itemId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    val quotation: Quotation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product? = null,

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    val quantity: java.math.BigDecimal = java.math.BigDecimal.ONE,

    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    val unitPrice: java.math.BigDecimal,

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    val totalPrice: java.math.BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    val itemType: ItemType,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)