package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.products.*
import org.prodacc.webapi.repositories.products.ProductCategoryRepository
import org.prodacc.webapi.repositories.products.ProductRepository
import org.prodacc.webapi.repositories.products.ProductVehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateProductCategoryDto
import org.prodacc.webapi.services.dataTransferObjects.CreateProductDto
import org.prodacc.webapi.services.dataTransferObjects.CreateProductVehicleDto
import org.prodacc.webapi.services.dataTransferObjects.toEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productVehicleRepository: ProductVehicleRepository
) {
    // Product operations
    fun getProductById(id: UUID): Product =
        productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with id: $id") }

    fun getProductWithRelationships(id: UUID): Product =
        productRepository.findByIdWithRelationships(id)
            .orElseThrow { EntityNotFoundException("Product not found with id: $id") }

    fun getAllProducts(): List<Product> =
        productRepository.findAll()

    fun getProductsByCategoryId(categoryId: UUID): List<Product> =
        productRepository.findProductsByCategoryId(categoryId)

    @Transactional
    fun createProduct(createProductDto: CreateProductDto): Product =
        productRepository.save(createProductDto.toEntity())

    @Transactional
    fun updateProduct(id: UUID, createProductDto: CreateProductDto): Product {
        val existingProduct = getProductById(id)
        val updatedProduct = createProductDto.toEntity().copy(
            id = existingProduct.id,
            version = existingProduct.version,
            productCategory = existingProduct.productCategory,
            productVehicle = existingProduct.productVehicle
        )
        return productRepository.save(updatedProduct)
    }

    @Transactional
    fun deleteProduct(id: UUID) {
        if (!productRepository.existsById(id)) {
            throw EntityNotFoundException("Product not found with id: $id")
        }
        productRepository.deleteById(id)
    }

    // Category operations
    fun getAllCategories(): List<ProductCategory> =
        productCategoryRepository.findAll()

    fun getAllCategoriesWithProducts(): List<ProductCategory> =
        productCategoryRepository.findAllWithProducts()

    fun getCategoryById(id: UUID): ProductCategory =
        productCategoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Category not found with id: $id") }

    fun getCategoryWithProducts(id: UUID): ProductCategory =
        productCategoryRepository.findByIdWithProducts(id)
            .orElseThrow { EntityNotFoundException("Category not found with id: $id") }

    @Transactional
    fun createCategory(createCategoryDto: CreateProductCategoryDto): ProductCategory =
        productCategoryRepository.save(createCategoryDto.toEntity())

    @Transactional
    fun updateCategory(id: UUID, createCategoryDto: CreateProductCategoryDto): ProductCategory {
        val existingCategory = getCategoryById(id)
        val updatedCategory = createCategoryDto.toEntity().copy(
            id = existingCategory.id,
            productReference = existingCategory.productReference
        )
        return productCategoryRepository.save(updatedCategory)
    }

    @Transactional
    fun deleteCategory(id: UUID) {
        if (!productCategoryRepository.existsById(id)) {
            throw EntityNotFoundException("Category not found with id: $id")
        }
        productCategoryRepository.deleteById(id)
    }

    // Vehicle operations
    fun getAllVehicles(): List<ProductVehicle> =
        productVehicleRepository.findAll()

    fun getAllVehiclesWithProducts(): List<ProductVehicle> =
        productVehicleRepository.findAllWithProducts()

    fun getVehicleById(id: UUID): ProductVehicle =
        productVehicleRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Vehicle not found with id: $id") }

    fun getVehicleWithProducts(id: UUID): ProductVehicle =
        productVehicleRepository.findByIdWithProducts(id)
            .orElseThrow { EntityNotFoundException("Vehicle not found with id: $id") }

    fun getVehiclesByMakeAndModel(make: String, model: String): List<ProductVehicle> =
        productVehicleRepository.findByMakeAndModel(make, model)

    fun getProductsByVehicleId(vehicleId: UUID): List<Product> =
        productRepository.findProductsByVehicleId(vehicleId)

    @Transactional
    fun createVehicle(createVehicleDto: CreateProductVehicleDto): ProductVehicle =
        productVehicleRepository.save(createVehicleDto.toEntity())

    @Transactional
    fun updateVehicle(id: UUID, createVehicleDto: CreateProductVehicleDto): ProductVehicle {
        val existingVehicle = getVehicleById(id)
        val updatedVehicle = createVehicleDto.toEntity().copy(
            id = existingVehicle.id,
            productReference = existingVehicle.productReference
        )
        return productVehicleRepository.save(updatedVehicle)
    }

    @Transactional
    fun deleteVehicle(id: UUID) {
        if (!productVehicleRepository.existsById(id)) {
            throw EntityNotFoundException("Vehicle not found with id: $id")
        }
        productVehicleRepository.deleteById(id)
    }

    // Relationship management
    @Transactional
    fun addCategoryToProduct(productId: UUID, categoryId: UUID) {
        val product = productRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found with id: $productId") }

        val category = productCategoryRepository.findById(categoryId)
            .orElseThrow { EntityNotFoundException("Category not found with id: $categoryId") }

        // Check if relationship already exists
        if (product.productCategory.none { it.categoryId.id == categoryId }) {
            val reference = ProductCategoryReference(
                productId = product,
                categoryId = category
            )
            product.productCategory.add(reference)
            productRepository.save(product)
        }
    }

    @Transactional
    fun removeCategoryFromProduct(productId: UUID, categoryId: UUID) {
        val product = getProductById(productId)
        val category = getCategoryById(categoryId)
        product.removeCategory(category)
        productRepository.save(product)
    }

    @Transactional
    fun addVehicleToProduct(productId: UUID, vehicleId: UUID) {
        val product = productRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found with id: $productId") }

        val vehicle = productVehicleRepository.findById(vehicleId)
            .orElseThrow { EntityNotFoundException("Vehicle not found with id: $vehicleId") }

        // Check if relationship already exists
        if (product.productVehicle.none { it.vehicleId.id == vehicleId }) {
            val reference = ProductVehicleReference(
                productId = product,
                vehicleId = vehicle
            )
            product.productVehicle.add(reference)
            productRepository.save(product)
        }
    }

    @Transactional
    fun removeVehicleFromProduct(productId: UUID, vehicleId: UUID) {
        val product = getProductById(productId)
        val vehicle = getVehicleById(vehicleId)
        product.removeVehicle(vehicle)
        productRepository.save(product)
    }
}