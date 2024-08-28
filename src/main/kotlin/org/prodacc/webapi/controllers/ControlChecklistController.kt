package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.ControlChecklistService
import org.prodacc.webapi.services.dataTransferObjects.NewControlChecklist
import org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/control-checklist")
class ControlChecklistController(
    private val controlChecklistService: ControlChecklistService
) {
    @GetMapping("/all")
    fun getChecklists() : Iterable<ResponseControlChecklist> =
        controlChecklistService.getChecklists()



    @GetMapping("/get-by-jobCard/{id}")
    fun getChecklistByJobCardId(@PathVariable id: UUID): ResponseControlChecklist =
        controlChecklistService.getChecklistByJobCardId(id)

    @GetMapping("/get-by-id/{id}")
    fun getChecklistById(@PathVariable id: UUID): ResponseControlChecklist =
        controlChecklistService.getChecklistById(id)

    @PostMapping("/new")
    fun newChecklist(@RequestBody checklist: NewControlChecklist): ResponseControlChecklist =
        controlChecklistService.newChecklist(checklist)

    @PutMapping("/update/{id}")
    fun updateChecklist(@PathVariable id: UUID, @RequestBody checklist: NewControlChecklist)
    : ResponseControlChecklist = controlChecklistService.updateChecklist(id, checklist)

    @DeleteMapping("/delete/{id}")
    fun deleteChecklist(@PathVariable id: UUID): ResponseEntity<String> =
        controlChecklistService.deleteChecklist(id)
}