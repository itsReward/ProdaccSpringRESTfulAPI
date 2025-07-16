package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ReportTemplate
import org.prodacc.webapi.models.ReportType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReportTemplateRepository : JpaRepository<ReportTemplate, UUID> {
    fun findByIsActiveTrue(): List<ReportTemplate>
    fun findByReportType(reportType: ReportType): List<ReportTemplate>
    fun findByTemplateNameContainingIgnoreCase(templateName: String): List<ReportTemplate>
}
