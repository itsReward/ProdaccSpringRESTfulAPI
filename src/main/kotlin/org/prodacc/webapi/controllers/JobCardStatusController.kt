package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.JobCardStatusService
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardStatusEntry
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/job-card-status")
class JobCardStatusController(
    private val jobCardStatusService: JobCardStatusService
){
    @GetMapping("/all")
    fun getAllJobCardStatus(): List<ResponseJobCardStatus> {
        return jobCardStatusService.getAllEntries()
    }

    @GetMapping("/get/{jobCardId}")
    fun getJobCardStatusRecords(@PathVariable("jobCardId") jobCardId : UUID) : List<ResponseJobCardStatus> {
        return jobCardStatusService.getAllJobCardStatusRecords(jobCardId)
    }

    @PostMapping("/newJobCardStatus")
    fun addJobCardStatus(@RequestBody newJobCardStatus: NewJobCardStatusEntry ) : ResponseJobCardStatus {
        return jobCardStatusService.newJobCardStatus(newJobCardStatus)
    }
}