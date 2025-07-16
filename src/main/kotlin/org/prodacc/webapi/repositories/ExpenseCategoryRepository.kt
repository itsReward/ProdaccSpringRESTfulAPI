package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ExpenseCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ExpenseCategoryRepository : JpaRepository<ExpenseCategory, UUID> {
    fun findByIsActiveTrue(): List<ExpenseCategory>
    fun findByCategoryNameContainingIgnoreCase(categoryName: String): List<ExpenseCategory>
    fun existsByCategoryNameIgnoreCase(categoryName: String): Boolean
}