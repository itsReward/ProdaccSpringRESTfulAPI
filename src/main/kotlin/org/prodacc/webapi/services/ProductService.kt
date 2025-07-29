package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Product
import org.prodacc.webapi.models.ProductCategory
import org.prodacc.webapi.models.TransactionType
import org.prodacc.webapi.repositories.ProductCategoryRepository
import org.prodacc.webapi.repositories.ProductRepository
import org.prodacc.webapi.repositories.ProductVehicleReferenceRepository
import org.prodacc.webapi.repositories.ProductVehicleRepository
import org.prodacc.webapi.repositories.SupplierRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateInventoryTransactionDto
import org.prodacc.webapi.services.dataTransferObjects.CreateProductDto
import org.prodacc.webapi.services.dataTransferObjects.ProductResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productVehicleReferenceRepository: ProductVehicleReferenceRepository,
    private val productVehicleRepository: ProductVehicleRepository,
    private val supplierRepository: SupplierRepository,
    private val inventoryTransactionService: InventoryTransactionService
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    fun getAllProducts(): List<ProductResponseDto> {
        logger.info("Fetching all active products")
        return productRepository.findByIsActiveTrue().map { it.toDto() }
    }

    fun getProductById(id: UUID): ProductResponseDto {
        logger.info("Fetching product with ID: $id")
        val product = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $id") }
        return product.toDto()
    }

    fun getProductWithRelationships(id: UUID): ProductResponseDto {
        logger.info("Fetching product with relationships for ID: $id")
        val product = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $id") }
        return product.toDto()
    }

    fun createProduct(createDto: CreateProductDto): ProductResponseDto {
        logger.info("Creating new product: ${createDto.productName}")

        if (productRepository.existsByProductCodeIgnoreCase(createDto.productCode)) {
            throw IllegalArgumentException("Product with code '${createDto.productCode}' already exists")
        }

        val category = createDto.categoryId?.let {
            productCategoryRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Product category not found with ID: $it") }
        }

        val supplier = createDto.supplierId?.let {
            supplierRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Supplier not found with ID: $it") }
        }

        val product = Product(
            productCode = createDto.productCode,
            productName = createDto.productName,
            description = createDto.description,
            category = category,
            brand = createDto.brand,
            unitOfMeasure = createDto.unitOfMeasure,
            minimumStock = createDto.minimumStock,
            maximumStock = createDto.maximumStock,
            costPrice = createDto.costPrice,
            sellingPrice = createDto.sellingPrice,
            markupPercentage = createDto.markupPercentage,
            supplier = supplier
        )

        val savedProduct = productRepository.save(product)
        logger.info("Successfully created product with ID: ${savedProduct.productId}")

        return savedProduct.toDto()
    }

    fun updateProduct(id: UUID, updateDto: CreateProductDto): ProductResponseDto {
        logger.info("Updating product with ID: $id")

        val existingProduct = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $id") }

        val category = updateDto.categoryId?.let {
            productCategoryRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Product category not found with ID: $it") }
        }

        val supplier = updateDto.supplierId?.let {
            supplierRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Supplier not found with ID: $it") }
        }

        val updatedProduct = existingProduct.copy(
            productCode = updateDto.productCode,
            productName = updateDto.productName,
            description = updateDto.description,
            category = category ,
            brand = updateDto.brand,
            unitOfMeasure = updateDto.unitOfMeasure,
            minimumStock = updateDto.minimumStock,
            maximumStock = updateDto.maximumStock,
            costPrice = updateDto.costPrice,
            sellingPrice = updateDto.sellingPrice,
            markupPercentage = updateDto.markupPercentage,
            supplier = supplier,
            updatedAt = LocalDateTime.now()
        )

        val savedProduct = productRepository.save(updatedProduct)
        logger.info("Successfully updated product with ID: $id")

        return savedProduct.toDto()
    }

    fun deleteProduct(id: UUID): String {
        logger.info("Deleting product with ID: $id")

        val product = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $id") }

        // Soft delete by setting isActive to false
        val deactivatedProduct = product.copy(isActive = false, updatedAt = LocalDateTime.now())
        productRepository.save(deactivatedProduct)

        logger.info("Successfully deactivated product with ID: $id")
        return "Product successfully deactivated"
    }

    fun getLowStockProducts(): List<ProductResponseDto> {
        logger.info("Fetching low stock products")
        return productRepository.findLowStockProducts().map { it.toDto() }
    }

    fun getProductsForVehicle(make: String, model: String?): List<ProductResponseDto> {
        logger.info("Fetching products for vehicle: $make $model")
        val vehicles = productVehicleRepository.findByVehicleMakeIgnoreCaseAndVehicleModelIgnoreCase(make, model?:"")
            .map { it.id!! }

        return productVehicleReferenceRepository.findByVehicleIds(vehicles).map { it.product }.map { it!!.toDto() }

    }

    fun updateStock(productId: UUID, newStock: Int, reason: String): ProductResponseDto {
        logger.info("Updating stock for product ID: $productId to $newStock")

        val product = productRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $productId") }

        val stockDifference = newStock - product.currentStock

        // Update product stock
        val updatedProduct = product.copy(
            currentStock = newStock,
            updatedAt = LocalDateTime.now()
        )
        val savedProduct = productRepository.save(updatedProduct)

        // Create inventory transaction
        val transactionDto = CreateInventoryTransactionDto(
            productId = productId,
            transactionType = if (stockDifference >= 0) TransactionType.ADJUSTMENT else TransactionType.ADJUSTMENT,
            quantity = stockDifference,
            notes = reason,
            referenceType = "MANUAL_ADJUSTMENT"
        )
        inventoryTransactionService.createTransaction(transactionDto)

        logger.info("Successfully updated stock for product ID: $productId")
        return savedProduct.toDto()
    }
}