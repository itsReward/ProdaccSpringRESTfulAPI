package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.JobCardTechniciansServices
import org.prodacc.webapi.services.dataTransferObjects.DeleteTechnician
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardTechnician
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardTechnicians
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/job-card-technicians")
class JobCardTechnicianController (
    private val jobCardTechniciansServices: JobCardTechniciansServices
) {
    @GetMapping("/allEntries")
    fun getAllEntries () = jobCardTechniciansServices.getAllEntries()

    @GetMapping("/getAllJobCardTechnicians/{id}")
    fun getAllJobCardTechnician(@PathVariable id : UUID): List<UUID?> {
        return jobCardTechniciansServices.getJobCardTechniciansByJobCardId(id)
    }

    @GetMapping("/getJobCardsByTechnician/{id}")
    fun getJobCardsByTechnician(@PathVariable id : UUID): List<UUID?> {
        return jobCardTechniciansServices.getJobCardsDoneByTechnician(id)
    }

    @PostMapping("/add-technician")
    fun addJobCardTechnician(@RequestBody newJobCardTechnician: NewJobCardTechnician): ResponseJobCardTechnicians {
        return jobCardTechniciansServices.addJobCardTechnician(newJobCardTechnician)
    }

    @DeleteMapping("/remove-technician/")
    fun deleteJobCardTechnician(@RequestBody record : DeleteTechnician): ResponseEntity<String> {
        return jobCardTechniciansServices.deleteJobCardTechniciansByEmployee(record)
    }

}

