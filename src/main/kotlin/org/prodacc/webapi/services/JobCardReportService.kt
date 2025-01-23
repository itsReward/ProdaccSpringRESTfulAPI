package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardReports
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardReportsRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.UpdateJobCardReport
import org.prodacc.webapi.services.synchronisation.WebSocketHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class JobCardReportService(
    private val jobCardReportsRepository: JobCardReportsRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository,
    private val webSocketHandler: WebSocketHandler,
) {
    fun getAllReports(): List<ResponseJobCardReport> {
        return jobCardReportsRepository.findAll().map { it.toResponseJobCardReport() }
    }

    @Transactional
    fun newReport(newJobCardReport: NewJobCardReport): ResponseJobCardReport {
        val report = jobCardReportsRepository.save(newJobCardReport.toJobCardReport()).toResponseJobCardReport()
        webSocketHandler.broadcastUpdate("NEW_JOB_CARD_REPORT", report.reportId)
        return report
    }

    @Transactional
    fun updateReport(id: UUID, updateJobCardReport: UpdateJobCardReport): ResponseJobCardReport {
        val oldReport =
            jobCardReportsRepository.findById(id).orElseThrow { EntityNotFoundException("Report $id not found") }
        val employee = updateJobCardReport.employeeId?.let {
            employeeRepository.findById(it).orElseThrow { EntityNotFoundException("Employee $it not found") }
        }
        val newReport = oldReport.copy(
            employee = employee ?: oldReport.employee,
            jobReport = updateJobCardReport.jobReport ?: oldReport.jobReport,
            typeOfReport = updateJobCardReport.reportType ?: oldReport.typeOfReport
        )
        val reponse = jobCardReportsRepository.save(newReport).toResponseJobCardReport()
        webSocketHandler.broadcastUpdate("UPDATE_JOB_CARD_REPORT", reponse.jobCardId)
        return reponse
    }

    fun getJobCardsReports(jobCardId: UUID): List<ResponseJobCardReport> {
        val jobCard =
            jobCardRepository.findById(jobCardId).orElseThrow { EntityNotFoundException("Job Card not found") }
        return jobCardReportsRepository.getJobCardReportsByJobCardId(jobCard).map { it.toResponseJobCardReport() }
    }

    fun getJobCardReport(reportId: UUID): ResponseJobCardReport {
        return jobCardReportsRepository.findById(reportId).orElseThrow { EntityNotFoundException("Report Not Found") }
            .toResponseJobCardReport()
    }

    @Transactional
    fun deleteJobCardReport(jobCardReportId: UUID): ResponseEntity<String> {
        val report = jobCardReportsRepository.findById(jobCardReportId)
            .orElseThrow { EntityNotFoundException("Report $jobCardReportId not found") }
        try {
            jobCardReportsRepository.delete(report)
            webSocketHandler.broadcastUpdate("DELETE_JOB_CARD_REPORT", report!!.jobCardId!!)
            return ResponseEntity("Job Card Report deleted", HttpStatus.OK)
        } catch (e: EntityNotFoundException) {
            throw (EntityNotFoundException(""))
        }
    }


    fun NewJobCardReport.toJobCardReport(): JobCardReports {
        val jobCard =
            jobCardRepository.findById(this.jobCardId).orElseThrow { EntityNotFoundException("Job Card not found") }
        val employee =
            employeeRepository.findById(this.employeeId).orElseThrow { EntityNotFoundException("Employee not found") }
        return JobCardReports(
            jobCardId = jobCard,
            employee = employee,
            jobReport = this.jobReport,
            typeOfReport = this.reportType
        )
    }

    fun JobCardReports.toResponseJobCardReport(): ResponseJobCardReport {
        val employee = employeeRepository.findById(this.employee?.let {
            it.employeeId ?: throw (IllegalArgumentException("Employee Id can not be null"))
        } ?: throw (IllegalArgumentException("Employee can not be null")))
            .orElseThrow { EntityNotFoundException("Employee not found") }
            .employeeId ?: throw (IllegalArgumentException("Employee ID can not be null"))

        val jobCard = jobCardRepository.findById(this.jobCardId?.let {
            it.jobId ?: throw (IllegalArgumentException("Job Card Id cannot be null"))
        } ?: throw (IllegalArgumentException("Job Card cannot be null")))
            .orElseThrow {
                EntityNotFoundException("Job Card can not be found")
            }
            .jobId ?: throw (IllegalArgumentException("Job ID cannot be null"))
        return ResponseJobCardReport(
            reportId = this.jobCardReportId ?: throw (IllegalArgumentException("Report id can not be null")),
            employeeId = employee,
            jobCardId = jobCard,
            jobReport = this.jobReport ?: throw (IllegalArgumentException("Report can not be null")),
            reportType = this.typeOfReport ?: throw (IllegalArgumentException("Report type can not be null"))
        )
    }
}