package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.dataTransferObjects.NewJobCard
import org.prodacc.webapi.services.dataTransferObjects.ViewJobCard
import org.prodacc.webapi.services.JobCardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/v1/jobCards/")
class JobCardController(
    private val jobCardService: JobCardService
) {
    @GetMapping("/all")
    fun getJobCards():Iterable<org.prodacc.webapi.services.dataTransferObjects.ViewJobCard>  = jobCardService.getJobCards()

    @GetMapping("/get/{id}")
    fun getJobCard(@PathVariable id: UUID): org.prodacc.webapi.services.dataTransferObjects.ViewJobCard = jobCardService.getJobCard(id)

    @PostMapping("/new")
    fun newJobCard(@RequestBody newJobCard: org.prodacc.webapi.services.dataTransferObjects.NewJobCard): org.prodacc.webapi.services.dataTransferObjects.ViewJobCard = jobCardService.newJobCard(newJobCard)

    @PutMapping("/update/{id}")
    fun updateJobCard(@PathVariable id: UUID, @RequestBody newJobCard: org.prodacc.webapi.services.dataTransferObjects.NewJobCard): org.prodacc.webapi.services.dataTransferObjects.ViewJobCard =
        jobCardService.updateJobCard(id, newJobCard)

    @DeleteMapping("/delete/{jobCardId}")
    fun deleteJobCard(@PathVariable jobCardId: UUID): ResponseEntity<String> =
        jobCardService.deleteJobCard(jobCardId)

}