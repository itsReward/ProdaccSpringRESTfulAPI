package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.InventoryTransaction
import org.prodacc.webapi.models.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreateInventoryTransactionDto(
    val productId: UUID,
    val transactionType: TransactionType,
    val quantity: Int,
    val unitCost: BigDecimal? = null,
    val referenceType: String? = null,
    val referenceId: UUID? = null,
    val notes: String? = null,
    val createdBy: UUID? = null
)

data class InventoryTransactionResponseDto(
    val transactionId: UUID,
    val productCode: String,
    val productName: String,
    val transactionType: TransactionType,
    val quantity: Int,
    val unitCost: BigDecimal?,
    val totalAmount: BigDecimal?,
    val referenceType: String?,
    val referenceId: UUID?,
    val transactionDate: LocalDateTime,
    val notes: String?,
    val createdBy: UUID?
)

fun InventoryTransaction.toDto(): InventoryTransactionResponseDto = InventoryTransactionResponseDto(
    transactionId = this.transactionId!!,
    productCode = this.product!!.productCode,
    productName = this.product.productName,
    transactionType = this.transactionType!!,
    quantity = this.quantity!!,
    unitCost = this.unitCost,
    totalAmount = this.totalAmount,
    referenceType = this.referenceType,
    referenceId = this.referenceId,
    transactionDate = this.transactionDate,
    notes = this.notes,
    createdBy = this.createdBy
)
