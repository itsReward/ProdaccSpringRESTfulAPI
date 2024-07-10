package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.VehicleControlChecklist
import org.prodacc.webapi.services.dataTransferObjects.NewControlChecklist
import org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist
import org.prodacc.webapi.repositories.ControlChecklistRepository
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class ControlChecklistService(
    private val controlChecklistRepository: ControlChecklistRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {
    private val logger = LoggerFactory.getLogger(ControlChecklistService::class.java)

    fun getChecklists() : Iterable<org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist> {
        logger.info("Getting all control checklists")
        return controlChecklistRepository.findAll().map { it.toResponseControlChecklist() }
    }



    fun getChecklistByJobCardId(id: UUID): org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist {
        logger.info("Getting control checklist by Job Card id: $id")
        val jobCard = jobCardRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Job Card not found with id: $id") }
        return controlChecklistRepository.findVehicleControlChecklistByJobCard(jobCard)
            .orElseThrow { EntityNotFoundException ("No control checklist associated with job card $id found") }
            .toResponseControlChecklist()
    }

    fun getChecklistById(id: UUID): org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist {
        logger.info("Getting control checklist by id: $id")
        return controlChecklistRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Control Checklist with id $id not found") }
            .toResponseControlChecklist()
    }

    fun newChecklist( checklist: org.prodacc.webapi.services.dataTransferObjects.NewControlChecklist): org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist {
        logger.info("Creating new control checklist")
        val jobCard = checklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Job Card not found with id: $it") }
        } ?: throw IllegalArgumentException("Job Card can not be null")
        val technician = checklist.technicianId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee not found with id: $it") }
        } ?: throw IllegalArgumentException("Employee can not be null")

        return controlChecklistRepository.save(
            VehicleControlChecklist(
                jobCard = jobCard,
                technician = technician,
                created = checklist.created,
                checklist = checklist.checklist
            )
        ).toResponseControlChecklist()
    }

    fun updateChecklist(id: UUID,checklist: org.prodacc.webapi.services.dataTransferObjects.NewControlChecklist): org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist {
        logger.info("Updating checklist with id: $id")
        val oldChecklist = controlChecklistRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Control Checklist not found with id: $id") }
        val jobCard = checklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Job Card not found with id: $it") }
        }
        val technician = checklist.technicianId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee not found with id: $it") }
        }
        val updatedChecklist = oldChecklist.copy(
            jobCard = jobCard ?: oldChecklist.jobCard,
            technician = technician ?: oldChecklist.technician,
            created = checklist.created ?: oldChecklist.created,
            checklist = checklist.checklist ?: oldChecklist.checklist
        )
        return controlChecklistRepository.save(updatedChecklist).toResponseControlChecklist()
    }

    fun deleteChecklist(id: UUID): ResponseEntity<String> {
        logger.info("Deleting checklist with id: $id")
        return if (controlChecklistRepository.existsById(id)) {
            controlChecklistRepository.deleteById(id)
            ResponseEntity("Control Checklist deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Checklist not found with id: $id")

        }
    }

    private fun VehicleControlChecklist.toResponseControlChecklist(): org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist {
        val jobCard = this.jobCard?.job_id?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("JobCard with id $it not found") }
        } ?: throw IllegalArgumentException("JobCard can not be null")
        val technician = this.technician?.employeeId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee with id $it not found") }
        } ?: throw IllegalArgumentException("Employee can not be null")
        return org.prodacc.webapi.services.dataTransferObjects.ResponseControlChecklist(
            id = this.id,
            jobCardId = jobCard.job_id,
            jobCardName = jobCard.jobCardName,
            technicianId = technician.employeeId,
            technicianName = technician.employeeName + " " + technician.employeeSurname,
            checklist = this.checklist,
            created = this.created
        )
    }


}