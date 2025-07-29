package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.DashboardService
import org.prodacc.webapi.services.dataTransferObjects.ActivityDto
import org.prodacc.webapi.services.dataTransferObjects.DashboardSummaryDto
import org.prodacc.webapi.services.dataTransferObjects.FinancialOverviewDto
import org.prodacc.webapi.services.dataTransferObjects.KeyMetricsDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = ["*"])
class DashboardController(
    private val dashboardService: DashboardService
) {

    @GetMapping("/summary")
    fun getDashboardSummary(): ResponseEntity<DashboardSummaryDto> =
        ResponseEntity.ok(dashboardService.getDashboardSummary())

    @GetMapping("/financial-overview")
    fun getFinancialOverview(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): ResponseEntity<FinancialOverviewDto> =
        ResponseEntity.ok(dashboardService.getFinancialOverview(startDate, endDate))

    @GetMapping("/recent-activities")
    fun getRecentActivities(@RequestParam(defaultValue = "20") limit: Int): ResponseEntity<List<ActivityDto>> =
        ResponseEntity.ok(dashboardService.getRecentActivities(limit))

    @GetMapping("/key-metrics")
    fun getKeyMetrics(): ResponseEntity<KeyMetricsDto> =
        ResponseEntity.ok(dashboardService.getKeyMetrics())
}