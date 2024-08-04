package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.JobCardReportService
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardReport
import org.prodacc.webapi.services.dataTransferObjects.UpdateJobCardReport
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping( "/api/v1/job-card-reports")
class JobCardReportsController (
    private val jobCardReportService: JobCardReportService
){
    @GetMapping("/getJobCardReports/{jobCardId}")
    fun getJobCardReports(@PathVariable("jobCardId") jobCardId : UUID): List<ResponseJobCardReport> {
        return jobCardReportService.getJobCardsReports(jobCardId)
    }

    @GetMapping("/getReportById/{id}")
    fun getJobCardReport(@PathVariable("id") id : UUID): ResponseJobCardReport? {
        return jobCardReportService.getJobCardReport(id)
    }

    @PostMapping("/new")
    fun addReport(@RequestBody newJobReport: NewJobCardReport): ResponseJobCardReport? {
        return jobCardReportService.newReport(newJobReport)
    }

    @PutMapping("/update/{reportId}")
    fun updateReport(@PathVariable reportId:UUID, @RequestBody updateJobCardReport : UpdateJobCardReport): ResponseJobCardReport? {
        return jobCardReportService.updateReport(reportId, updateJobCardReport)
    }

    @DeleteMapping("/delete/{reportId}")
    fun updateReport(@PathVariable reportId:UUID): ResponseEntity<String> {
        return jobCardReportService.deleteJobCardReport(reportId)
    }
}