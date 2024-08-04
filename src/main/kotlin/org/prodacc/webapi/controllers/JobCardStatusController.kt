package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.JobCardStatusService
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardStatus
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/job-card-status")
class JobCardStatusController(
    private val jobCardStatusService: JobCardStatusService
){
    @GetMapping("/get/{jobCardId}")
    fun getJobCardStatusRecords(@PathVariable("jobCardId") jobCardId : UUID) : List<ResponseJobCardStatus> {
        return jobCardStatusService.getAllJobCardStatusRecords(jobCardId)
    }

    @PostMapping("/newJobCardStatus")
    fun addJobCardStatus(@RequestBody newJobCardStatus: NewJobCardStatus ) : ResponseJobCardStatus {
        return jobCardStatusService.addJobCardStatus(newJobCardStatus)
    }
}