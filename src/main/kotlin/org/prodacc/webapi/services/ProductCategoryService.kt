package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.repositories.ProductCategoryRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateProductCategoryDto
import org.prodacc.webapi.services.dataTransferObjects.ProductCategoryResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.prodacc.webapi.services.dataTransferObjects.toEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class ProductCategoryService(
    private val productCategoryRepository: ProductCategoryRepository
) {
    private val logger = LoggerFactory.getLogger(ProductCategoryService::class.java)

    fun getAllCategories(): List<ProductCategoryResponseDto> =
        productCategoryRepository.findAll().map { it.toDto() }

    fun getCategoryById(id: UUID): ProductCategoryResponseDto {
        val category = productCategoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product category not found with ID: $id") }
        return category.toDto()
    }

    fun createCategory(createDto: CreateProductCategoryDto): ProductCategoryResponseDto {
        if (productCategoryRepository.existsByCategoryNameIgnoreCase(createDto.categoryName)) {
            throw IllegalArgumentException("Category with name '${createDto.categoryName}' already exists")
        }
        val category = createDto.toEntity()
        val savedCategory = productCategoryRepository.save(category)
        return savedCategory.toDto()
    }

    fun updateCategory(id: UUID, updateDto: CreateProductCategoryDto): ProductCategoryResponseDto {
        val existingCategory = productCategoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product category not found with ID: $id") }

        val updatedCategory = existingCategory.copy(
            categoryName = updateDto.categoryName,
            description = updateDto.description
        )
        val savedCategory = productCategoryRepository.save(updatedCategory)
        return savedCategory.toDto()
    }

    fun deleteCategory(id: UUID): String {
        if (!productCategoryRepository.existsById(id)) {
            throw EntityNotFoundException("Product category not found with ID: $id")
        }
        productCategoryRepository.deleteById(id)
        return "Category successfully deleted"
    }
}