package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Expense
import org.prodacc.webapi.models.ExpenseCategory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class CreateExpenseCategoryDto(
    val categoryName: String,
    val description: String? = null
)

data class CreateExpenseDto(
    val categoryId: UUID,
    val description: String,
    val amount: BigDecimal,
    val expenseDate: LocalDate = LocalDate.now(),
    val receiptNumber: String? = null,
    val supplierId: UUID? = null,
    val isRecurring: Boolean = false,
    val recurrencePeriod: String? = null,
    val notes: String? = null,
    val createdBy: UUID? = null
)

data class ExpenseResponseDto(
    val expenseId: UUID,
    val categoryName: String,
    val description: String,
    val amount: BigDecimal,
    val expenseDate: LocalDate,
    val receiptNumber: String?,
    val supplierName: String?,
    val isRecurring: Boolean,
    val recurrencePeriod: String?,
    val notes: String?,
    val createdAt: LocalDateTime
)

fun Expense.toDto(): ExpenseResponseDto = ExpenseResponseDto(
    expenseId = this.expenseId!!,
    categoryName = this.category.categoryName,
    description = this.description,
    amount = this.amount,
    expenseDate = this.expenseDate,
    receiptNumber = this.receiptNumber,
    supplierName = this.supplier?.supplierName,
    isRecurring = this.isRecurring,
    recurrencePeriod = this.recurrencePeriod,
    notes = this.notes,
    createdAt = this.createdAt
)

data class ExpenseCategoryResponseDto(
    val categoryId: UUID,
    val categoryName: String,
    val description: String?,
    val isActive: Boolean,
    val expenseCount: Int = 0
)

fun ExpenseCategory.toDto(): ExpenseCategoryResponseDto = ExpenseCategoryResponseDto(
    categoryId = this.categoryId!!,
    categoryName = this.categoryName,
    description = this.description,
    isActive = this.isActive,
    expenseCount = this.expenses.size
)