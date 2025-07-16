package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Product
import org.prodacc.webapi.models.ProductCategory
import org.prodacc.webapi.models.Supplier
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByIsActiveTrue(): List<Product>
    fun findByIsActiveTrueAndCurrentStockLessThanEqual(threshold: Int): List<Product>
    fun findByProductCodeIgnoreCase(productCode: String): Product?
    fun findByProductNameContainingIgnoreCase(productName: String): List<Product>
    fun findByCategory(category: ProductCategory): List<Product>
    fun findBySupplier(supplier: Supplier): List<Product>
    fun existsByProductCodeIgnoreCase(productCode: String): Boolean

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.currentStock <= p.minimumStock")
    fun findLowStockProducts(): List<Product>

    @Query("SELECT p FROM Product p JOIN ProductVehicle pv ON p.productId = pv.product.productId " +
            "WHERE pv.vehicleMake = :make AND (:model IS NULL OR pv.vehicleModel = :model)")
    fun findProductsForVehicle(@Param("make") make: String, @Param("model") model: String?): List<Product>

    @Query("SELECT SUM(p.currentStock * p.costPrice) FROM Product p WHERE p.isActive = true")
    fun calculateTotalInventoryValue(): BigDecimal?
}