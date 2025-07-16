package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.InventoryTransaction
import org.prodacc.webapi.models.Product
import org.prodacc.webapi.models.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface InventoryTransactionRepository : JpaRepository<InventoryTransaction, UUID> {
    fun findByProduct(product: Product): List<InventoryTransaction>
    fun findByTransactionType(transactionType: TransactionType): List<InventoryTransaction>
    fun findByTransactionDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<InventoryTransaction>
    fun findByReferenceTypeAndReferenceId(referenceType: String, referenceId: UUID): List<InventoryTransaction>

    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.productId = :productId " +
            "ORDER BY it.transactionDate DESC")
    fun findRecentTransactionsByProduct(@Param("productId") productId: UUID, pageable: Pageable): Page<InventoryTransaction>

    @Query("SELECT SUM(CASE WHEN it.transactionType IN ('PURCHASE', 'ADJUSTMENT') AND it.quantity > 0 THEN it.quantity " +
            "WHEN it.transactionType IN ('SALE', 'ADJUSTMENT') AND it.quantity < 0 THEN it.quantity " +
            "ELSE 0 END) FROM InventoryTransaction it WHERE it.product.productId = :productId")
    fun calculateStockForProduct(@Param("productId") productId: UUID): Int?
}