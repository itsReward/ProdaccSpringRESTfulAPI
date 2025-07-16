package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.InventoryTransaction
import org.prodacc.webapi.models.Product
import org.prodacc.webapi.models.TransactionType
import org.prodacc.webapi.repositories.InventoryTransactionRepository
import org.prodacc.webapi.repositories.ProductRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateInventoryTransactionDto
import org.prodacc.webapi.services.dataTransferObjects.InventoryTransactionResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class InventoryTransactionService(
    private val inventoryTransactionRepository: InventoryTransactionRepository,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(InventoryTransactionService::class.java)

    fun createTransaction(createDto: CreateInventoryTransactionDto): InventoryTransactionResponseDto {
        logger.info("Creating inventory transaction for product: ${createDto.productId}")

        val product = productRepository.findById(createDto.productId)
            .orElseThrow { EntityNotFoundException("Product not found with ID: ${createDto.productId}") }

        val totalAmount = createDto.unitCost?.multiply(BigDecimal(createDto.quantity))

        val transaction = InventoryTransaction(
            product = product,
            transactionType = createDto.transactionType,
            quantity = createDto.quantity,
            unitCost = createDto.unitCost,
            totalAmount = totalAmount,
            referenceType = createDto.referenceType,
            referenceId = createDto.referenceId,
            notes = createDto.notes,
            createdBy = createDto.createdBy
        )

        val savedTransaction = inventoryTransactionRepository.save(transaction)

        // Update product stock based on transaction type
        updateProductStock(product, createDto.transactionType, createDto.quantity)

        logger.info("Successfully created inventory transaction with ID: ${savedTransaction.transactionId}")
        return savedTransaction.toDto()
    }

    private fun updateProductStock(product: Product, transactionType: TransactionType, quantity: Int) {
        val newStock = when (transactionType) {
            TransactionType.PURCHASE, TransactionType.RETURN -> product.currentStock + quantity
            TransactionType.SALE -> product.currentStock - quantity
            TransactionType.ADJUSTMENT -> product.currentStock + quantity // quantity can be negative
            TransactionType.TRANSFER -> product.currentStock - quantity
        }

        val updatedProduct = product.copy(
            currentStock = maxOf(0, newStock), // Ensure stock doesn't go negative
            updatedAt = LocalDateTime.now()
        )
        productRepository.save(updatedProduct)
    }

    fun getTransactionsByProduct(productId: UUID): List<InventoryTransactionResponseDto> {
        logger.info("Fetching transactions for product: $productId")
        val product = productRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $productId") }

        return inventoryTransactionRepository.findByProduct(product).map { it.toDto() }
    }

    fun getRecentTransactions(limit: Int = 50): List<InventoryTransactionResponseDto> {
        logger.info("Fetching recent inventory transactions (limit: $limit)")
        val pageable = PageRequest.of(0, limit)
        return inventoryTransactionRepository.findAll(pageable).content.map { it.toDto() }
    }
}