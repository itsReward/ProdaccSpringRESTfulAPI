package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.repositories.AppointmentRepository
import org.prodacc.webapi.repositories.ExpenseRepository
import org.prodacc.webapi.repositories.InvoiceRepository
import org.prodacc.webapi.repositories.ProductRepository
import org.prodacc.webapi.services.dataTransferObjects.ActivityDto
import org.prodacc.webapi.services.dataTransferObjects.CreateSupplierDto
import org.prodacc.webapi.services.dataTransferObjects.DashboardSummaryDto
import org.prodacc.webapi.services.dataTransferObjects.FinancialOverviewDto
import org.prodacc.webapi.services.dataTransferObjects.KeyMetricsDto
import org.prodacc.webapi.services.dataTransferObjects.SupplierResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.util.UUID

@Service
class DashboardService(
    private val invoiceRepository: InvoiceRepository,
    private val expenseRepository: ExpenseRepository,
    private val appointmentRepository: AppointmentRepository,
    private val productRepository: ProductRepository,
) {

    fun getDashboardSummary(): DashboardSummaryDto {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)

        return DashboardSummaryDto(
            totalRevenue = invoiceRepository.calculateRevenue(startOfMonth, today) ?: BigDecimal.ZERO,
            totalExpenses = expenseRepository.calculateTotalExpenses(startOfMonth, today) ?: BigDecimal.ZERO,
            outstandingInvoices = invoiceRepository.calculateTotalOutstanding() ?: BigDecimal.ZERO,
            todayAppointments = appointmentRepository.countTodaysAppointments(),
            lowStockProducts = productRepository.findLowStockProducts().size,
            overdueInvoices = invoiceRepository.findOverdueInvoices().size
        )
    }

    fun getFinancialOverview(startDate: String?, endDate: String?): FinancialOverviewDto {
        val start = startDate?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now().withDayOfMonth(1)
        val end = endDate?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now()

        val revenue = invoiceRepository.calculateRevenue(start, end) ?: java.math.BigDecimal.ZERO
        val expenses = expenseRepository.calculateTotalExpenses(start, end) ?: java.math.BigDecimal.ZERO
        val profit = revenue.subtract(expenses)
        val profitMargin = if (revenue > java.math.BigDecimal.ZERO)
            profit.divide(revenue, 4, java.math.RoundingMode.HALF_UP).multiply(java.math.BigDecimal(100))
        else java.math.BigDecimal.ZERO

        return FinancialOverviewDto(
            revenue = revenue,
            expenses = expenses,
            profit = profit,
            profitMargin = profitMargin
        )
    }

    fun getRecentActivities(limit: Int): List<ActivityDto> {
        // This would typically combine recent activities from various entities
        // For now, return an empty list - to be implemented based on requirements
        return emptyList()
    }

    fun getKeyMetrics(): KeyMetricsDto {
        // These would be calculated based on business logic
        // For now, return default values - to be implemented based on requirements
        return KeyMetricsDto(
            averageJobValue = BigDecimal.ZERO,
            customerRetentionRate = BigDecimal.ZERO,
            inventoryTurnover = BigDecimal.ZERO,
            paymentCollectionRate = BigDecimal.ZERO
        )
    }
}