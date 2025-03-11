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
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    // Product operations
    fun getProductById(id: UUID): Product {
        logger.info("fetching product with id $id")
        return productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with id: $id") }
    }

    fun getProductWithRelationships(id: UUID): Product {
        logger.info("get product with id $id and relationships")
        return productRepository.findByIdWithRelationships(id)
            .orElseThrow { EntityNotFoundException("Product not found with id: $id") }
    }

    fun getAllProducts(): List<Product> {
        logger.info("fetching all products")
        return productRepository.findAll()
    }

    fun getProductsByCategoryId(categoryId: UUID): List<Product> {
        logger.info("fetching products by category id $categoryId")
        return productRepository.findProductsByCategoryId(categoryId)
    }

    @Transactional
    fun createProduct(createProductDto: CreateProductDto): Product {
        logger.info("creating new product")
        return productRepository.save(createProductDto.toEntity())
    }

    @Transactional
    fun updateProduct(id: UUID, createProductDto: CreateProductDto): Product {
        logger.info("Updating existing product with id $id")
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
        logger.info("deleting product with id $id")
        if (!productRepository.existsById(id)) {
            throw EntityNotFoundException("Product not found with id: $id")
        }
        productRepository.deleteById(id)
    }

    // Category operations
    fun getAllCategories(): List<ProductCategory> {
        logger.info("fetching categories")
        return productCategoryRepository.findAll()
    }

    fun getAllCategoriesWithProducts(): List<ProductCategory> {
        logger.info("fetching categories with products")
        return productCategoryRepository.findAllWithProducts()
    }

    fun getCategoryById(id: UUID): ProductCategory {
        logger.info("fetching category with id $id")
        return productCategoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Category not found with id: $id") }
    }

    fun getCategoryWithProducts(id: UUID): ProductCategory {
        logger.info("fetching category with id $id and products")
        return productCategoryRepository.findByIdWithProducts(id)
            .orElseThrow { EntityNotFoundException("Category not found with id: $id") }
    }

    @Transactional
    fun createCategory(createCategoryDto: CreateProductCategoryDto): ProductCategory {
        logger.info("creating product")
        return productCategoryRepository.save(createCategoryDto.toEntity())
    }

    @Transactional
    fun updateCategory(id: UUID, createCategoryDto: CreateProductCategoryDto): ProductCategory {
        logger.info("Updating product with id $id")
        val existingCategory = getCategoryById(id)
        val updatedCategory = createCategoryDto.toEntity().copy(
            id = existingCategory.id,
            productReference = existingCategory.productReference
        )
        return productCategoryRepository.save(updatedCategory)
    }

    @Transactional
    fun deleteCategory(id: UUID) {
        logger.info("deleting category with id $id")
        if (!productCategoryRepository.existsById(id)) {
            throw EntityNotFoundException("Category not found with id: $id")
        }
        productCategoryRepository.deleteById(id)
    }

    // Vehicle operations
    fun getAllVehicles(): List<ProductVehicle> {
        logger.info("fetching all vehicles")
        return productVehicleRepository.findAll()
    }

    fun getAllVehiclesWithProducts(): List<ProductVehicle> {
        logger.info("fetching all vehicles with products")
        return productVehicleRepository.findAllWithProducts()
    }

    fun getVehicleById(id: UUID): ProductVehicle {
        logger.info("fetching vehicle with id $id")
        return productVehicleRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Vehicle not found with id: $id") }
    }

    fun getVehicleWithProducts(id: UUID): ProductVehicle {
        logger.info("fetching vehicle with id $id and products")
        return productVehicleRepository.findByIdWithProducts(id)
            .orElseThrow { EntityNotFoundException("Vehicle not found with id: $id") }
    }

    fun getVehiclesByMakeAndModel(make: String, model: String): List<ProductVehicle> {
        logger.info("Get Vehicles with their models")
        return productVehicleRepository.findByMakeAndModel(make, model)
    }

    fun getProductsByVehicleId(vehicleId: UUID): List<Product> {
        logger.info("getting products by vehicle id $vehicleId")
        return productRepository.findProductsByVehicleId(vehicleId)
    }

    @Transactional
    fun createVehicle(createVehicleDto: CreateProductVehicleDto): ProductVehicle {
        logger.info("create vehicle")
        return productVehicleRepository.save(createVehicleDto.toEntity())
    }

    @Transactional
    fun updateVehicle(id: UUID, createVehicleDto: CreateProductVehicleDto): ProductVehicle {
        logger.info("Updating vehicle with id $id")
        val existingVehicle = getVehicleById(id)
        val updatedVehicle = createVehicleDto.toEntity().copy(
            id = existingVehicle.id,
            productReference = existingVehicle.productReference
        )
        return productVehicleRepository.save(updatedVehicle)
    }

    @Transactional
    fun deleteVehicle(id: UUID) {
        logger.info("deleting vehicle with id $id")
        if (!productVehicleRepository.existsById(id)) {
            throw EntityNotFoundException("Vehicle not found with id: $id")
        }
        productVehicleRepository.deleteById(id)
    }

    // Relationship management
    @Transactional
    fun addCategoryToProduct(productId: UUID, categoryId: UUID) {
        logger.info("Adding category to product")
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
        logger.info("Removing category from product")
        val product = getProductById(productId)
        val category = getCategoryById(categoryId)
        product.removeCategory(category)
        productRepository.save(product)
    }

    @Transactional
    fun addVehicleToProduct(productId: UUID, vehicleId: UUID) {
        logger.info("Adding a vehicle to a product")
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
        logger.info("Removing vehicle from product")
        val product = getProductById(productId)
        val vehicle = getVehicleById(vehicleId)
        product.removeVehicle(vehicle)
        productRepository.save(product)
    }
}