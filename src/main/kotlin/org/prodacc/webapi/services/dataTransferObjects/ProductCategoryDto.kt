package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.ProductCategory
import java.time.LocalDateTime
import java.util.UUID

data class CreateProductCategoryDto(
    val categoryName: String,
    val description: String? = null
)

data class ProductCategoryResponseDto(
    val categoryId: UUID,
    val categoryName: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val productCount: Int = 0
)

fun ProductCategory.toDto(): ProductCategoryResponseDto = ProductCategoryResponseDto(
    categoryId = this.categoryId!!,
    categoryName = this.categoryName,
    description = this.description,
    createdAt = this.createdAt,
    productCount = this.products.size
)

fun CreateProductCategoryDto.toEntity(): ProductCategory = ProductCategory(
    categoryName = this.categoryName,
    description = this.description
)
