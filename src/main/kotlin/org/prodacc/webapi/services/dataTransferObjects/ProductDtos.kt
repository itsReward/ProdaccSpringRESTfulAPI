package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Product
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreateProductDto(
    val productCode: String,
    val productName: String,
    val description: String? = null,
    val categoryId: UUID? = null,
    val brand: String? = null,
    val unitOfMeasure: String? = null,
    val minimumStock: Int = 0,
    val maximumStock: Int = 1000,
    val costPrice: BigDecimal = BigDecimal.ZERO,
    val sellingPrice: BigDecimal = BigDecimal.ZERO,
    val markupPercentage: BigDecimal = BigDecimal.ZERO,
    val supplierId: UUID? = null
)

data class ProductResponseDto(
    val productId: UUID,
    val productCode: String,
    val productName: String,
    val description: String?,
    val categoryName: String?,
    val brand: String?,
    val unitOfMeasure: String?,
    val currentStock: Int,
    val minimumStock: Int,
    val maximumStock: Int,
    val costPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val markupPercentage: BigDecimal,
    val supplierName: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isLowStock: Boolean = false
)

data class CreateProductVehicleDto(
    val productId: UUID,
    val vehicleMake: String,
    val vehicleModel: String? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null
)

fun Product.toDto(): ProductResponseDto = ProductResponseDto(
    productId = this.productId!!,
    productCode = this.productCode,
    productName = this.productName,
    description = this.description,
    categoryName = this.category?.categoryName,
    brand = this.brand,
    unitOfMeasure = this.unitOfMeasure,
    currentStock = this.currentStock,
    minimumStock = this.minimumStock,
    maximumStock = this.maximumStock,
    costPrice = this.costPrice,
    sellingPrice = this.sellingPrice,
    markupPercentage = this.markupPercentage,
    supplierName = this.supplier?.supplierName,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    isLowStock = this.currentStock <= this.minimumStock
)
