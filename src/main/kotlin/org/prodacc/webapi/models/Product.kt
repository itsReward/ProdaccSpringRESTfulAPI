package org.prodacc.webapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "product_id", nullable = false)
    val productId: UUID? = null,

    @Column(name = "product_code", nullable = false, unique = true)
    val productCode: String,

    @Column(name = "product_name", nullable = false)
    val productName: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    val category: ProductCategory? = null,

    @Column(name = "brand")
    val brand: String? = null,

    @Column(name = "unit_of_measure")
    val unitOfMeasure: String? = null,

    @Column(name = "current_stock")
    var currentStock: Int = 0,

    @Column(name = "minimum_stock")
    val minimumStock: Int = 0,

    @Column(name = "maximum_stock")
    val maximumStock: Int = 1000,

    @Column(name = "cost_price", precision = 12, scale = 2)
    val costPrice: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "selling_price", precision = 12, scale = 2)
    val sellingPrice: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "markup_percentage", precision = 5, scale = 2)
    val markupPercentage: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    val supplier: Supplier? = null,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val vehicleCompatibility: List<ProductVehicle> = emptyList(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val inventoryTransactions: List<InventoryTransaction> = emptyList()
)