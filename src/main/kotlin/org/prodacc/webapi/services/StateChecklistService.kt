package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.VehicleStateChecklist
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.StateChecklistRepository
import org.prodacc.webapi.services.dataTransferObjects.NewVehicleStateChecklist
import org.prodacc.webapi.services.dataTransferObjects.ResponseStateChecklist
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class StateChecklistService(
    private val stateChecklistRepository: StateChecklistRepository,
    private val jobCardRepository: JobCardRepository,
) {
    private val logger = LoggerFactory.getLogger(StateChecklistService::class.java.name)

    fun getAllChecklists() : Iterable<ResponseStateChecklist> {
        logger.info("Getting all checklists")
        return stateChecklistRepository.findAll().map { it.toResponseStateChecklist() }
    }

    fun getChecklistById(id: UUID): ResponseStateChecklist {
        logger.info("Getting checklist by id: $id")
        return stateChecklistRepository.findById(id)
            .orElseThrow { EntityNotFoundException("State checklist with id: $id not found") }
            .toResponseStateChecklist()
    }

    fun getChecklistByJobCardId( id: UUID): ResponseStateChecklist {
        logger.info("Getting checklist associated with JobCard: $id")

        val jobCard = jobCardRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Job card with id: $id not found") }
        return stateChecklistRepository.findVehicleStateChecklistByJobCard(jobCard)
            .get().toResponseStateChecklist()
    }

    @Transactional
    fun newStateChecklist( newStateChecklist: NewVehicleStateChecklist) : ResponseStateChecklist {
        logger.info("Creating new state checklist")
        val jobCard = newStateChecklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Job card with id: ${newStateChecklist.jobCardId} not found") }
        } ?: throw IllegalArgumentException("Job card can not be null")
        return stateChecklistRepository.save(
            VehicleStateChecklist(
                jobCard = jobCard,
                millageIn = newStateChecklist.millageIn,
                millageOut = newStateChecklist.millageOut,
                fuelLevelIn = newStateChecklist.fuelLevelIn,
                fuelLevelOut = newStateChecklist.fuelLevelOut,
                checklist = newStateChecklist.checklist,
                created = newStateChecklist.created
            )
        ).toResponseStateChecklist()
    }

    @Transactional
    fun updateStateChecklist( id: UUID, newStateChecklist: NewVehicleStateChecklist) : ResponseStateChecklist {
        logger.info("Updating state checklist with id: $id")
        val oldChecklist = stateChecklistRepository.findById(id)
            .orElseThrow { EntityNotFoundException("State checklist with id: $id not found") }
        val jobCard = newStateChecklist.jobCardId?.let {
            jobCardRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Job card with id: $id not found") }
        }
        val newChecklist = oldChecklist.copy(
            jobCard = jobCard ?: oldChecklist.jobCard,
            millageIn = newStateChecklist.millageIn ?: oldChecklist.millageIn,
            millageOut = newStateChecklist.millageOut ?: oldChecklist.millageOut,
            fuelLevelIn = newStateChecklist.fuelLevelIn ?: oldChecklist.fuelLevelIn,
            fuelLevelOut = newStateChecklist.fuelLevelOut ?: oldChecklist.fuelLevelOut,
            checklist = newStateChecklist.checklist ?: oldChecklist.checklist,
            created = newStateChecklist.created ?: oldChecklist.created
        )

        return stateChecklistRepository.save(newChecklist).toResponseStateChecklist()
    }

    @Transactional
    fun deleteStateChecklist(id: UUID) : ResponseEntity<String> {
        logger.info("Deleting state checklist with id: $id")
        return if (stateChecklistRepository.existsById(id)) {
            stateChecklistRepository.deleteById(id)
            ResponseEntity("State Checklist deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("State checklist with id: $id does not exist")

        }
    }

    private fun VehicleStateChecklist.toResponseStateChecklist(): ResponseStateChecklist {
        val jobCard = this.jobCard?.job_id
            .let {
                if (it != null) {
                    jobCardRepository.findById(it)
                        .orElseThrow { EntityNotFoundException("JobCard with Id: ${this.jobCard?.job_id} not found") }
                } else {
                    throw NullPointerException("State checklist must be associated with a JobCard!!, enter jobCard id")
                }
            } ?: throw NullPointerException("state checklist must be associated with a JobCard!!, enter jobCard id")

        return ResponseStateChecklist(
            id = this.id,
            jobCardId = jobCard.job_id,
            jobCardName = jobCard.jobCardName,
            millageIn = this.millageIn,
            millageOut = this.millageOut,
            fuelLevelIn = this.fuelLevelIn,
            fuelLevelOut = this.fuelLevelOut,
            created = this.created,
            checklist = this.checklist
        )

    }

}