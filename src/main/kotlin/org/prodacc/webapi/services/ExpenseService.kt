package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.Expense
import org.prodacc.webapi.models.ExpenseCategory
import org.prodacc.webapi.repositories.ExpenseCategoryRepository
import org.prodacc.webapi.repositories.ExpenseRepository
import org.prodacc.webapi.repositories.SupplierRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateExpenseCategoryDto
import org.prodacc.webapi.services.dataTransferObjects.CreateExpenseDto
import org.prodacc.webapi.services.dataTransferObjects.ExpenseCategoryResponseDto
import org.prodacc.webapi.services.dataTransferObjects.ExpenseResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class ExpenseService(
    private val expenseRepository: ExpenseRepository,
    private val expenseCategoryRepository: ExpenseCategoryRepository,
    private val supplierRepository: SupplierRepository
) {
    private val logger = LoggerFactory.getLogger(ExpenseService::class.java)

    fun getAllExpenses(): List<ExpenseResponseDto> =
        expenseRepository.findAll().map { it.toDto() }

    fun getExpenseById(id: UUID): ExpenseResponseDto {
        val expense = expenseRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Expense not found with ID: $id") }
        return expense.toDto()
    }

    fun createExpense(createDto: CreateExpenseDto): ExpenseResponseDto {
        val category = expenseCategoryRepository.findById(createDto.categoryId)
            .orElseThrow { EntityNotFoundException("Expense category not found with ID: ${createDto.categoryId}") }

        val supplier = createDto.supplierId?.let {
            supplierRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Supplier not found with ID: $it") }
        }

        val expense = Expense(
            category = category,
            description = createDto.description,
            amount = createDto.amount,
            expenseDate = createDto.expenseDate,
            receiptNumber = createDto.receiptNumber,
            supplier = supplier,
            isRecurring = createDto.isRecurring,
            recurrencePeriod = createDto.recurrencePeriod,
            notes = createDto.notes,
            createdBy = createDto.createdBy
        )

        val savedExpense = expenseRepository.save(expense)
        return savedExpense.toDto()
    }

    fun updateExpense(id: UUID, updateDto: CreateExpenseDto): ExpenseResponseDto {
        val existingExpense = expenseRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Expense not found with ID: $id") }

        val category = expenseCategoryRepository.findById(updateDto.categoryId)
            .orElseThrow { EntityNotFoundException("Expense category not found with ID: ${updateDto.categoryId}") }

        val supplier = updateDto.supplierId?.let {
            supplierRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Supplier not found with ID: $it") }
        }

        val updatedExpense = existingExpense.copy(
            category = category,
            description = updateDto.description,
            amount = updateDto.amount,
            expenseDate = updateDto.expenseDate,
            receiptNumber = updateDto.receiptNumber,
            supplier = supplier,
            isRecurring = updateDto.isRecurring,
            recurrencePeriod = updateDto.recurrencePeriod,
            notes = updateDto.notes
        )

        val savedExpense = expenseRepository.save(updatedExpense)
        return savedExpense.toDto()
    }

    fun deleteExpense(id: UUID): String {
        if (!expenseRepository.existsById(id)) {
            throw EntityNotFoundException("Expense not found with ID: $id")
        }
        expenseRepository.deleteById(id)
        return "Expense successfully deleted"
    }

    fun getAllCategories(): List<ExpenseCategoryResponseDto> =
        expenseCategoryRepository.findByIsActiveTrue().map { it.toDto() }

    fun createCategory(createDto: CreateExpenseCategoryDto): ExpenseCategoryResponseDto {
        if (expenseCategoryRepository.existsByCategoryNameIgnoreCase(createDto.categoryName)) {
            throw IllegalArgumentException("Category with name '${createDto.categoryName}' already exists")
        }

        val category = ExpenseCategory(
            categoryName = createDto.categoryName,
            description = createDto.description
        )

        val savedCategory = expenseCategoryRepository.save(category)
        return savedCategory.toDto()
    }
}
