package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.NewJobCard
import org.prodacc.webapi.models.dataTransferObjects.ViewJobCard
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
    fun getJobCards():Iterable<ViewJobCard>  = jobCardService.getJobCards()

    @GetMapping("/get/{id}")
    fun getJobCard(@PathVariable id: UUID): ViewJobCard = jobCardService.getJobCard(id)

    @PostMapping("/new")
    fun newJobCard(@RequestBody newJobCard: NewJobCard): ViewJobCard = jobCardService.newJobCard(newJobCard)

    @PutMapping("/update/{id}")
    fun updateJobCard(@PathVariable id: UUID, @RequestBody newJobCard: NewJobCard): ViewJobCard =
        jobCardService.updateJobCard(id, newJobCard)

    @DeleteMapping("/delete/{jobCardId}")
    fun deleteJobCard(@PathVariable jobCardId: UUID): ResponseEntity<String> =
        jobCardService.deleteJobCard(jobCardId)

}