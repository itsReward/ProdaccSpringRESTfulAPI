/*
package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.products.Product
import org.prodacc.webapi.models.products.ProductCategory
import org.prodacc.webapi.models.products.ProductVehicle
import java.util.*


// DTOs
@Deprecated("Create Product Deprecated update to the new one")
data class CreateProductDto(
    val partNumber: String,
    val partName: String,
    val description: String?,
    val inStock: Float,
    val healthyNumber: Float,
    val arrivalPrice: Float,
    val sellingPrice: String,
    val storageLocation: String
)

@Deprecated("Product Response Deprecated, update to the new one")
data class ProductResponseDto(
    val id: UUID,
    val partNumber: String,
    val partName: String,
    val description: String?,
    val inStock: Float,
    val healthyNumber: Float,
    val arrivalPrice: Float,
    val sellingPrice: String,
    val storageLocation: String,
    val version: Long,
    val categories: List<ProductCategoryResponseDto>,
    val vehicles: List<ProductVehicleResponseDto>
)

data class CreateProductCategoryDto(
    val name: String,
    val description: String?
)

data class ProductCategoryResponseDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val productCount: Int
)

data class ProductCategoryWithProductsResponseDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val products: List<ProductResponseDto>
)

data class CreateProductVehicleDto(
    val make: String,
    val model: String,
    val year: Int
)

data class ProductVehicleResponseDto(
    val id: UUID,
    val make: String,
    val model: String,
    val year: Int,
    val productCount: Int
)

data class ProductVehicleWithProductsResponseDto(
    val id: UUID,
    val make: String,
    val model: String,
    val year: Int,
    val products: List<ProductResponseDto>
)

// Mapping functions
fun CreateProductDto.toEntity() = Product(
    id = UUID.randomUUID(),
    partNumber = partNumber,
    partName = partName,
    description = description ?: "",
    inStock = inStock,
    healthyNumber = healthyNumber,
    arrivalPrice = arrivalPrice,
    sellingPrice = sellingPrice,
    storageLocation = storageLocation,
    version = 0
)

fun CreateProductCategoryDto.toEntity() = ProductCategory(
    id = UUID.randomUUID(),
    name = name,
    description = description ?: ""
)

fun CreateProductVehicleDto.toEntity() = ProductVehicle(
    id = UUID.randomUUID(),
    make = make,
    model = model,
    year = year
)

fun Product.toDto() = ProductResponseDto(
    id = id,
    partNumber = partNumber,
    partName = partName,
    description = description,
    inStock = inStock,
    healthyNumber = healthyNumber,
    arrivalPrice = arrivalPrice,
    sellingPrice = sellingPrice,
    storageLocation = storageLocation,
    version = version?: 0,
    categories = productCategory.map { it.categoryId.toDto() },
    vehicles = productVehicle.map { it.vehicleId.toDto() }
)

fun ProductCategory.toDto() = ProductCategoryResponseDto(
    id = id,
    name = name,
    description = description,
    productCount = productReference.size
)

fun ProductCategory.toDtoWithProducts() = ProductCategoryWithProductsResponseDto(
    id = id,
    name = name,
    description = description,
    products = productReference.map { it.productId.toDto() }
)

fun ProductVehicle.toDto() = ProductVehicleResponseDto(
    id = id,
    make = make,
    model = model,
    year = year,
    productCount = productReference.size
)

fun ProductVehicle.toDtoWithProducts() = ProductVehicleWithProductsResponseDto(
    id = id,
    make = make,
    model = model,
    year = year,
    products = productReference.map { it.productId.toDto() }
)*/
