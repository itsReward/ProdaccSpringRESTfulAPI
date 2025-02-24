package org.prodacc.webapi.models.products

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"product_id\"", nullable = false)
    val id: UUID,

    @Column(name = "\"part_number\"", nullable = false, length = 100)
    val partNumber: String,

    @Column(name = "\"part_name\"", nullable = false, length = 100)
    val partName: String,

    @Column(name = "description", nullable = true, length = 100)
    val description: String,

    @Column(name = "in_stock", nullable = false)
    val inStock: Float,

    @Column(name = "healthy_number", nullable = false)
    val healthyNumber: Float,

    @Column(name = "arrival_price", nullable = false)
    val arrivalPrice: Float,

    @Column(name = "selling_price", nullable = false)
    val sellingPrice: String,

    @Column(name = "storage_location", nullable = false)
    val storageLocation: String,

    @Version
    @Column(name = "version", nullable = false)
    val version: Long? = 0,

    @OneToMany(mappedBy = "productId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val productVehicle: MutableSet<ProductVehicleReference> = mutableSetOf(),

    @OneToMany(mappedBy = "productId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val productCategory: MutableSet<ProductCategoryReference> = mutableSetOf()

) {
    internal constructor(): this(
        id = UUID.randomUUID(),
        partNumber = "",
        partName = "",
        description = "",
        inStock = 0.0f,
        healthyNumber = 0.0f,
        arrivalPrice = 0.0f,
        sellingPrice = "",
        storageLocation = "",
        productVehicle = mutableSetOf(),
        productCategory = mutableSetOf()
    )


    override fun toString(): String = "Product(id=$id, partNumber='$partNumber', partName='$partName')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

// Extension functions to manage relationships
fun Product.addCategory(category: ProductCategory) {
    val reference = ProductCategoryReference(productId = this, categoryId = category)
    productCategory.add(reference)
    category.productReference.add(reference)
}

fun Product.removeCategory(category: ProductCategory) {
    val reference = productCategory.find { it.categoryId == category }
    if (reference != null) {
        productCategory.remove(reference)
        category.productReference.remove(reference)
    }
}

fun Product.addVehicle(vehicle: ProductVehicle) {
    val reference = ProductVehicleReference(productId = this, vehicleId = vehicle)
    productVehicle.add(reference)
    vehicle.productReference.add(reference)
}

fun Product.removeVehicle(vehicle: ProductVehicle) {
    val reference = productVehicle.find { it.vehicleId == vehicle }
    if (reference != null) {
        productVehicle.remove(reference)
        vehicle.productReference.remove(reference)
    }
}