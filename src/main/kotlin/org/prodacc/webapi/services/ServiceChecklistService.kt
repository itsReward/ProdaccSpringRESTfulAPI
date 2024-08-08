package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.VehicleServiceChecklist
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.ServiceChecklistRepository
import org.prodacc.webapi.services.dataTransferObjects.NewServiceChecklist
import org.prodacc.webapi.services.dataTransferObjects.ResponseServiceChecklistWithJobCard
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ServiceChecklistService(
    private val vehicleServiceChecklistRepository: ServiceChecklistRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {
    private val logger = LoggerFactory.getLogger(ServiceChecklistService::class.java)

    fun getAll(): Iterable<ResponseServiceChecklistWithJobCard> {
        logger.info("Getting all service checklists")
        return vehicleServiceChecklistRepository.findAll().map { it.toResponseServiceChecklistWithJobCard() }
    }

    fun getServiceChecklistByJobCardId(id: UUID): ResponseServiceChecklistWithJobCard {
        logger.info("Getting vehicle service checklists associated with Job Card ID $id")
        val jobCard = jobCardRepository.findById(id)
            .orElseThrow {EntityNotFoundException("$id Job Card Not Found")}
        val vehicleChecklist = vehicleServiceChecklistRepository
            .getVehicleServiceChecklistByJobCard(jobCard)
            .get()
        return vehicleChecklist.toResponseServiceChecklistWithJobCard()
    }


    fun getServiceChecklistById(id: UUID): ResponseServiceChecklistWithJobCard {
        logger.info("Getting service checklists with : $id")
        return vehicleServiceChecklistRepository.findById(id).orElseThrow {
            EntityNotFoundException("Checklist with id $id not found")
        }.toResponseServiceChecklistWithJobCard()
    }


    @Transactional
    fun createServiceChecklist(newChecklist: NewServiceChecklist): ResponseServiceChecklistWithJobCard {
        logger.info("Creating a new service checklist")
        val jobCard = newChecklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow {EntityNotFoundException("${newChecklist.jobCardId} Job Card Not Found")}
        } ?: throw IllegalArgumentException("Job Card not be null")
        val technician = newChecklist.technician?.let {
            employeeRepository.findById(it)
                .orElseThrow {EntityNotFoundException("${newChecklist.technician} Employee Not Found")}
        } ?: throw IllegalArgumentException("Employee can not be null")
        val vehicleServiceChecklist = VehicleServiceChecklist(
            jobCard = jobCard,
            technician = technician,
            created = newChecklist.created,
            checklist = newChecklist.checklist
        )

        return vehicleServiceChecklistRepository.save(vehicleServiceChecklist).toResponseServiceChecklistWithJobCard()

    }

    @Transactional
    fun updateServiceChecklist(id: UUID, newChecklist: NewServiceChecklist): ResponseServiceChecklistWithJobCard {
        logger.info("Updating a service checklist with id $id")
        val oldServiceChecklist = vehicleServiceChecklistRepository.findById(id)
            .orElseThrow {EntityNotFoundException("$id Service Checklist Not Found")}
        val jobCard = newChecklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("${newChecklist.jobCardId} Job Card Not Found") }
        }

        val technician = newChecklist.technician?.let {
            employeeRepository.findById(it)
                .orElseThrow {EntityNotFoundException("${newChecklist.technician} Job Card Not Found")}
        }

        val updatedServiceChecklist = oldServiceChecklist.copy(
            jobCard = jobCard ?: oldServiceChecklist.jobCard,
            technician = technician ?: oldServiceChecklist.technician,
            created = newChecklist.created ?: oldServiceChecklist.created,
            checklist = newChecklist.checklist ?: oldServiceChecklist.checklist
        )

        return vehicleServiceChecklistRepository.save(updatedServiceChecklist).toResponseServiceChecklistWithJobCard()
    }

    @Transactional
    fun deleteServiceChecklist( id: UUID): ResponseEntity<String> {
        return if (vehicleServiceChecklistRepository.existsById(id)) {
            vehicleServiceChecklistRepository.deleteById(id)
            ResponseEntity("Checklist deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("$id Service Checklist Not Found")
        }
    }

    private fun VehicleServiceChecklist.toResponseServiceChecklistWithJobCard(): ResponseServiceChecklistWithJobCard {
        val jobCard = this.jobCard?.jobId?.let {
            jobCardRepository.findById(it).orElseThrow { EntityNotFoundException("Job Card with id: $it not found") }
        } ?: throw IllegalArgumentException("JobCard cannot be null")
        val technician = this.technician?.employeeId?.let {
            employeeRepository.findById(it).orElseThrow { EntityNotFoundException("Employee with ID: $it not found") }
        } ?: throw IllegalArgumentException("Employee cannot be null")
        return ResponseServiceChecklistWithJobCard(
            id = this.id,
            jobCardName = jobCard.jobCardName,
            jobCardId = jobCard.jobId,
            technicianId = technician.employeeId,
            technicianName = technician.employeeName + " " + technician.employeeSurname,
            created = this.created,
            checklist = this.checklist

        )
    }


}