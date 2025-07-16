package org.prodacc.webapi.services.dataTransferObjects

import java.util.UUID

data class DashboardSummaryDto(
    val totalRevenue: java.math.BigDecimal,
    val totalExpenses: java.math.BigDecimal,
    val outstandingInvoices: java.math.BigDecimal,
    val todayAppointments: Long,
    val lowStockProducts: Int,
    val overdueInvoices: Int
)

data class FinancialOverviewDto(
    val revenue: java.math.BigDecimal,
    val expenses: java.math.BigDecimal,
    val profit: java.math.BigDecimal,
    val profitMargin: java.math.BigDecimal
)

data class ActivityDto(
    val type: String,
    val description: String,
    val timestamp: java.time.LocalDateTime,
    val entityId: UUID?
)

data class KeyMetricsDto(
    val averageJobValue: java.math.BigDecimal,
    val customerRetentionRate: java.math.BigDecimal,
    val inventoryTurnover: java.math.BigDecimal,
    val paymentCollectionRate: java.math.BigDecimal
)