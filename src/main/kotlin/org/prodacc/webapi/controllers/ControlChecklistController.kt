package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.VehicleControlChecklist
import org.prodacc.webapi.models.dataTransferObjects.NewControlChecklist
import org.prodacc.webapi.repositories.ControlChecklistRepository
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.Optional
import java.util.UUID

@RestController
@RequestMapping("api/v1/control-checklist")
class ControlChecklistController(
    private val repository: ControlChecklistRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {
    @GetMapping("/all")
    fun getChecklists() : Iterable<VehicleControlChecklist> = repository.findAll()



    @GetMapping("/get-by-jobCard/{id}")
    fun getChecklistByJobCardId(@PathVariable id: UUID): Optional<VehicleControlChecklist> {
        return repository.findVehicleControlChecklistByJobCard(jobCardRepository.findById(id).get())
    }

    @GetMapping("/get-by-id/{id}")
    fun getChecklistById(@PathVariable id: UUID): Optional<VehicleControlChecklist> {
        return repository.findById(id)
    }

    @PostMapping("/new")
    fun newChecklist(@RequestBody checklist: NewControlChecklist): VehicleControlChecklist {
        val jobCard = jobCardRepository.findById(checklist.jobCardId)
        val technician = employeeRepository.findById(checklist.technicianId)

        return repository.save(
            VehicleControlChecklist(
                jobCard = jobCard.get(),
                technician = technician.get(),
                created = checklist.created,
                checklist = checklist.checklist
            )
        )
    }

    @PutMapping("/update/{id}")
    fun updateChecklist(@PathVariable id: UUID, @RequestBody checklist: NewControlChecklist): VehicleControlChecklist {
        val oldChecklist = repository.findById(id)
        val jobCard = jobCardRepository.findById(checklist.jobCardId)
        val technician = employeeRepository.findById(checklist.technicianId)
        val updatedChecklist = oldChecklist.get().copy(
            jobCard = jobCard.get(),
            technician = technician.get(),
            created = checklist.created,
            checklist = checklist.checklist
        )
        return repository.save(updatedChecklist)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteChecklist(@PathVariable id: UUID): ResponseEntity<String> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity("Control Checklist deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("Control Checklist not found", HttpStatus.NOT_FOUND)

        }
    }
}