package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.VehicleServiceChecklist
import org.prodacc.webapi.models.dataTransferObjects.NewServiceChecklist
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.ServiceChecklistRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.UUID

@RestController
@RequestMapping("/v1/api/checklist")
class ServiceChecklistController (
    private val vehicleServiceChecklistRepository: ServiceChecklistRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {

    @GetMapping("/all")
    fun getAll(): Iterable<VehicleServiceChecklist> = vehicleServiceChecklistRepository.findAll()

    @GetMapping("/get/{id}")
    fun getVehicleServiceChecklistByJobCardId(@PathVariable id: UUID): VehicleServiceChecklist = getVehicleServiceChecklistByJobCardId(id)

    @PostMapping("/create")
    fun createVehicleServiceChecklist(newChecklist: NewServiceChecklist): ResponseEntity<VehicleServiceChecklist> {

        return if (jobCardRepository.findById(newChecklist.jobCardId).isPresent && employeeRepository.findById(newChecklist.technician).isPresent ){

            ResponseEntity(
                vehicleServiceChecklistRepository.save(
                    VehicleServiceChecklist(
                        jobCard = jobCardRepository.findById(newChecklist.jobCardId).get(),
                        technician = employeeRepository.findById(newChecklist.technician).get(),
                        created = newChecklist.created.toInstant(ZoneId.systemDefault().rules.getOffset(newChecklist.created)),
                        checklist = newChecklist.checklist
                    )
                ),
                HttpStatus.CREATED
            )
        } else ResponseEntity.status(HttpStatus.CONFLICT).build()


    }

    @DeleteMapping("/{id}")
    fun deleteVehicleServiceChecklist(@PathVariable id: UUID): ResponseEntity<String> {
        return if (vehicleServiceChecklistRepository.existsById(id)) {
            vehicleServiceChecklistRepository.deleteById(id)
            ResponseEntity("Checklist deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("Checklist not found", HttpStatus.NOT_FOUND)
        }
    }
}