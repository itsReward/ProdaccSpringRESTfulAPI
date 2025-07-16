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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "invoice_items")
data class InvoiceItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "item_id", nullable = false)
    val itemId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val invoice: Invoice,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    val product: Product? = null,

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    val quantity: BigDecimal = BigDecimal.ONE,

    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    val unitPrice: BigDecimal,

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    val totalPrice: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 50)
    val itemType: ItemType,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    // Business logic methods
    fun calculateTotalPrice(): BigDecimal {
        return unitPrice.multiply(quantity)
    }

    fun getProductCode(): String? = product?.productCode

    fun getProductName(): String? = product?.productName

    fun getFormattedQuantity(): String {
        return if (quantity.stripTrailingZeros().scale() <= 0) {
            quantity.toInt().toString()
        } else {
            quantity.stripTrailingZeros().toPlainString()
        }
    }

    fun getFormattedUnitPrice(): String {
        return "$${unitPrice.setScale(2, java.math.RoundingMode.HALF_UP)}"
    }

    fun getFormattedTotalPrice(): String {
        return "$${totalPrice.setScale(2, java.math.RoundingMode.HALF_UP)}"
    }

    fun isLabor(): Boolean = itemType == ItemType.LABOR

    fun isPart(): Boolean = itemType == ItemType.PART

    fun isService(): Boolean = itemType == ItemType.SERVICE

    fun isConsumable(): Boolean = itemType == ItemType.CONSUMABLE

    // Equals and hashCode based on itemId
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InvoiceItem) return false
        return itemId == other.itemId
    }

    override fun hashCode(): Int {
        return itemId?.hashCode() ?: 0
    }

    // ToString method for debugging
    override fun toString(): String {
        return "InvoiceItem(itemId=$itemId, description='$description', " +
                "quantity=$quantity, unitPrice=$unitPrice, totalPrice=$totalPrice, " +
                "itemType=$itemType)"
    }
}

// Item Type Enum
enum class ItemType {
    LABOR,
    PART,
    CONSUMABLE,
    SERVICE;

    fun getDisplayName(): String {
        return when (this) {
            LABOR -> "Labor"
            PART -> "Part"
            CONSUMABLE -> "Consumable"
            SERVICE -> "Service"
        }
    }

    fun getIcon(): String {
        return when (this) {
            LABOR -> "👷"
            PART -> "🔧"
            CONSUMABLE -> "🛢️"
            SERVICE -> "⚙️"
        }
    }

    fun isInventoryItem(): Boolean {
        return this in listOf(PART, CONSUMABLE)
    }

    fun requiresStockUpdate(): Boolean {
        return isInventoryItem()
    }
}