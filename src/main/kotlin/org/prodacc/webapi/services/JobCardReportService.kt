package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardReports
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardReportsRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.UpdateJobCardReport
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobCardReportService(
    private val jobCardReportsRepository: JobCardReportsRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository,
) {
    fun getAllReports(): List<ResponseJobCardReport> {
        return jobCardReportsRepository.findAll().map { it.toResponseJobCardReport() }
    }

    fun newReport(newJobCardReport: NewJobCardReport): ResponseJobCardReport {
        return jobCardReportsRepository.save(newJobCardReport.toJobCardReport()).toResponseJobCardReport()
    }

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
        return jobCardReportsRepository.save(newReport).toResponseJobCardReport()
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

    fun deleteJobCardReport(jobCardReportId: UUID): ResponseEntity<String> {
        val report = jobCardReportsRepository.findById(jobCardReportId)
            .orElseThrow { EntityNotFoundException("Report $jobCardReportId not found") }
        try {
            jobCardReportsRepository.delete(report)
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
            it.job_id ?: throw (IllegalArgumentException("Job Card Id cannot be null"))
        } ?: throw (IllegalArgumentException("Job Card cannot be null")))
            .orElseThrow {
                EntityNotFoundException("Job Card can not be found")
            }
            .job_id ?: throw (IllegalArgumentException("Job ID cannot be null"))
        return ResponseJobCardReport(
            reportId = this.jobCardReportId ?: throw (IllegalArgumentException("Report id can not be null")),
            employeeId = employee,
            jobCardId = jobCard,
            jobReport = this.jobReport ?: throw (IllegalArgumentException("Report can not be null")),
            reportType = this.typeOfReport ?: throw (IllegalArgumentException("Report type can not be null"))
        )
    }
}