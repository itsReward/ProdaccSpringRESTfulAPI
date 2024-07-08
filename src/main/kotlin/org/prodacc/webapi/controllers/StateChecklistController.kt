package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.VehicleControlChecklist
import org.prodacc.webapi.models.VehicleStateChecklist
import org.prodacc.webapi.models.dataTransferObjects.NewVehicleStateChecklist
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.StateChecklistRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1/state-checklist")
class StateChecklistController (
    private val stateChecklistRepository: StateChecklistRepository,
    private val jobCardRepository: JobCardRepository,
){
    @GetMapping("/all")
    fun getAllChecklists() : Iterable<VehicleStateChecklist> = stateChecklistRepository.findAll()

    @GetMapping("/get-by-id/{id}")
    fun getChecklistById(@PathVariable id: UUID): Optional<VehicleStateChecklist> = stateChecklistRepository.findById(id)

    @GetMapping("/get-by-jobCard/{id}")
    fun getChecklistByJobCardId(@PathVariable id: UUID): Optional<VehicleStateChecklist> =
        stateChecklistRepository.findVehicleStateChecklistByJobCard(jobCardRepository.findById(id).get())

    @PostMapping("/new")
    fun newStateChecklist(@RequestBody newStateChecklist: NewVehicleStateChecklist) : VehicleStateChecklist {
        return stateChecklistRepository.save(
            VehicleStateChecklist(
                jobCard = jobCardRepository.findById(newStateChecklist.jobCardId).get(),
                millageIn = newStateChecklist.millageIn,
                millageOut = newStateChecklist.millageOut,
                fuelLevelIn = newStateChecklist.fuelLevelIn,
                fuelLevelOut = newStateChecklist.fuelLevelOut,
                checklist = newStateChecklist.checklist,
                created = newStateChecklist.created
            )
        )
    }

    @PutMapping("/update/{id}")
    fun updateStateChecklist(@PathVariable id: UUID, @RequestBody newStateChecklist: NewVehicleStateChecklist) : VehicleStateChecklist {
        val oldChecklist = stateChecklistRepository.findById(id)
        val newChecklist = oldChecklist.get().copy(
            jobCard = jobCardRepository.findById(newStateChecklist.jobCardId).get(),
            millageIn = newStateChecklist.millageIn,
            millageOut = newStateChecklist.millageOut,
            fuelLevelIn = newStateChecklist.fuelLevelIn,
            fuelLevelOut = newStateChecklist.fuelLevelOut,
            checklist = newStateChecklist.checklist,
            created = newStateChecklist.created
        )

        return stateChecklistRepository.save(
            newChecklist
        )
    }

    @DeleteMapping("/delete/{id}")
    fun deleteStateChecklist(@PathVariable id: UUID) : ResponseEntity<Any> {
        return if (stateChecklistRepository.existsById(id)) {
            stateChecklistRepository.deleteById(id)
            ResponseEntity("State Checklist deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("State Checklist not found", HttpStatus.NOT_FOUND)

        }
    }
}