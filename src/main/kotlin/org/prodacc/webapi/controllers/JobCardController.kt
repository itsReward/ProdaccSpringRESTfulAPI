package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.JobCardService
import org.prodacc.webapi.services.dataTransferObjects.NewJobCard
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCard
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/jobCards/")
class JobCardController(
    private val jobCardService: JobCardService
) {
    @GetMapping("/all")
    fun getJobCards():Iterable<ResponseJobCard>  = jobCardService.getJobCards()

    @GetMapping("/get/{id}")
    fun getJobCard(@PathVariable id: UUID): ResponseJobCard = jobCardService.getJobCard(id)

    @PostMapping("/new")
    fun newJobCard(@RequestBody newJobCard: NewJobCard): ResponseJobCard = jobCardService.newJobCard(newJobCard)

    @PutMapping("/update/{id}")
    fun updateJobCard(@PathVariable id: UUID, @RequestBody newJobCard: NewJobCard): ResponseJobCard =
        jobCardService.updateJobCard(id, newJobCard)

    @DeleteMapping("/delete/{jobCardId}")
    fun deleteJobCard(@PathVariable jobCardId: UUID): ResponseEntity<String> =
        jobCardService.deleteJobCard(jobCardId)

}