package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.NewServiceChecklist
import org.prodacc.webapi.models.dataTransferObjects.ResponseServiceChecklistWithJobCard
import org.prodacc.webapi.services.ServiceChecklistService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/api/service-checklist")
class ServiceChecklistController (
    private val serviceChecklistService: ServiceChecklistService,
) {

    @GetMapping("/all")
    fun getAll(): Iterable<ResponseServiceChecklistWithJobCard> = serviceChecklistService.getAll()

    @GetMapping("/get/{id}")
    fun getServiceChecklistById(@PathVariable id: UUID): ResponseServiceChecklistWithJobCard =
        serviceChecklistService.getServiceChecklistById(id)

    @GetMapping("/get/jobCard/{id}")
    fun getVehicleServiceChecklistByJobCardId(@PathVariable id: UUID): ResponseServiceChecklistWithJobCard =
        serviceChecklistService.getServiceChecklistByJobCardId(id)


    @PostMapping("/create")
    fun createVehicleServiceChecklist(newChecklist: NewServiceChecklist): ResponseServiceChecklistWithJobCard =
        serviceChecklistService.createServiceChecklist(newChecklist)


    @PutMapping("/update/{id}")
    fun updateVehicleServiceChecklist(@PathVariable id: UUID, @RequestBody newChecklist: NewServiceChecklist)
    : ResponseServiceChecklistWithJobCard = serviceChecklistService.updateServiceChecklist(id, newChecklist)


    @DeleteMapping("/delete/{id}")
    fun deleteVehicleServiceChecklist(@PathVariable id: UUID): ResponseEntity<String> =
        serviceChecklistService.deleteServiceChecklist(id)

}