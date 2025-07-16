package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Expense
import org.prodacc.webapi.models.ExpenseCategory
import org.prodacc.webapi.models.Supplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Repository
interface ExpenseRepository : JpaRepository<Expense, UUID> {
    fun findByCategory(category: ExpenseCategory): List<Expense>
    fun findBySupplier(supplier: Supplier): List<Expense>
    fun findByExpenseDateBetween(startDate: LocalDate, endDate: LocalDate): List<Expense>
    fun findByIsRecurringTrue(): List<Expense>

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    fun calculateTotalExpenses(@Param("startDate") startDate: LocalDate,
                               @Param("endDate") endDate: LocalDate): BigDecimal?

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
            "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    fun getExpensesByCategory(@Param("startDate") startDate: LocalDate,
                              @Param("endDate") endDate: LocalDate): List<Array<Any>>

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.expenseDate = CURRENT_DATE")
    fun countTodaysExpenses(): Long
}
