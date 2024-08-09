package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.StateChecklistService
import org.prodacc.webapi.services.dataTransferObjects.NewVehicleStateChecklist
import org.prodacc.webapi.services.dataTransferObjects.ResponseStateChecklist
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1/state-checklist")
class StateChecklistController (
    private val stateChecklistService: StateChecklistService,
){
    @GetMapping("/all")
    fun getAllChecklists() : Iterable<ResponseStateChecklist> = stateChecklistService.getAllChecklists()

    @GetMapping("/get-by-id/{id}")
    fun getChecklistById(@PathVariable id: UUID): ResponseStateChecklist =
        stateChecklistService.getChecklistById(id)

    @GetMapping("/get-by-jobCard/{id}")
    fun getChecklistByJobCardId(@PathVariable id: UUID): ResponseStateChecklist =
        stateChecklistService.getChecklistByJobCardId(id)

    @PostMapping("/new")
    fun newStateChecklist(@RequestBody newStateChecklist: NewVehicleStateChecklist) : ResponseStateChecklist =
        stateChecklistService.newStateChecklist(newStateChecklist)

    @PutMapping("/update/{id}")
    fun updateStateChecklist(@PathVariable id: UUID, @RequestBody newStateChecklist: NewVehicleStateChecklist)
    : ResponseStateChecklist =
        stateChecklistService.updateStateChecklist(id, newStateChecklist)

    @DeleteMapping("/delete/{id}")
    fun deleteStateChecklist(@PathVariable id: UUID) : ResponseEntity<String> =
        stateChecklistService.deleteStateChecklist(id)
}