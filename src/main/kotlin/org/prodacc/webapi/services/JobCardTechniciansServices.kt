package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardTechnicians
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.JobCardTechniciansRepository
import org.prodacc.webapi.services.dataTransferObjects.DeleteTechnician
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardTechnician
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardTechnicians
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class JobCardTechniciansServices(
    private val jobCardTechniciansRepository: JobCardTechniciansRepository,
    private val employeeRepository: EmployeeRepository,
    private val jobCardRepository: JobCardRepository,
) {
    fun getAllEntries(): List<ResponseJobCardTechnicians> {
        return jobCardTechniciansRepository.findAll().map { it.toResponseJobCardTechnicians() }
    }

    @Transactional
    fun addJobCardTechnician(newJobCardTechnician: NewJobCardTechnician): ResponseJobCardTechnicians {
        val technician = employeeRepository.findById(newJobCardTechnician.technicianId)
            .orElseThrow { EntityNotFoundException("Technician with id ${newJobCardTechnician.technicianId} Not Found") }
        val jobCard = jobCardRepository.findById(newJobCardTechnician.jobCardId)
            .orElseThrow { EntityNotFoundException("Job Card with id ${newJobCardTechnician.jobCardId} Not Found") }
        return jobCardTechniciansRepository.save(JobCardTechnicians(jobCard, technician)).toResponseJobCardTechnicians()
    }

    fun getJobCardTechniciansByJobCardId(jobCardId: UUID): List<UUID?> {
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job Card with id $jobCardId not found") }
        val jobCardWithTechnicians = jobCardTechniciansRepository.getJobCardTechniciansByJobCardId(jobCard)

        return jobCardWithTechnicians.map {
            employeeRepository.findById(it.employeeId?.let { employee ->
                employee.employeeId ?: throw (IllegalArgumentException("Employee Id can not be null"))
            } ?: throw (IllegalArgumentException("Employee can not be null")))
                .orElseThrow { EntityNotFoundException("Technician Not Found") }.employeeId
        }
    }

    fun getJobCardsDoneByTechnician(technicianId: UUID): List<UUID?> {
        val technician =
            employeeRepository.findById(technicianId).orElseThrow { EntityNotFoundException("Technician Not Found") }
        val jobCardWithTechnicians = jobCardTechniciansRepository.getJobCardTechniciansByEmployeeId(technician)


        return jobCardWithTechnicians.map {
            jobCardRepository.findById(it.jobCardId?.let { jobCard ->
                jobCard.jobId ?: throw (IllegalArgumentException("Job Card Not Found"))
            } ?: throw (IllegalArgumentException("Job Card with id $it.jobId Not Found")))
                .orElseThrow { EntityNotFoundException("Job Cards Not Found") }.jobId
        }
    }

    @Transactional
    fun deleteJobCardTechniciansByEmployee(record: DeleteTechnician): ResponseEntity<String> {
        val technician = employeeRepository.findById(record.technicianId)
            .orElseThrow { EntityNotFoundException("Technician with is ${record.technicianId} not found ") }
        val jobCard = jobCardRepository.findById(record.jobCardId)
            .orElseThrow { EntityNotFoundException("JobCard with id ${record.technicianId} not found") }

        val jobCardTechnicianRecord =
            jobCardTechniciansRepository.getJobCardTechniciansByEmployeeIdAndJobCardId(technician, jobCard)
        try {
            jobCardTechniciansRepository.delete(jobCardTechnicianRecord)
            return ResponseEntity("Technician Removed", HttpStatus.OK)
        } catch (_: EntityNotFoundException) {
            return ResponseEntity("Technician Record Not Found", HttpStatus.NOT_FOUND)
        }
    }

    fun JobCardTechnicians.toResponseJobCardTechnicians(): ResponseJobCardTechnicians {
        val jobCard = this.jobCardId?.let {
            jobCardRepository.findById(it.jobId ?: throw (IllegalArgumentException("Job ID is null")))
                .orElseThrow { EntityNotFoundException("JobCard with id ${it.jobId} not found") }
        }
            ?: throw (NullPointerException("Job Card returned null, JobCardTechnicians can not have a null jobcard value"))

        val technician = this.employeeId?.let {
            employeeRepository.findById(it.employeeId ?: throw (IllegalArgumentException("Employee ID is null")))
                .orElseThrow { EntityNotFoundException("Employee ID is null") }
        }
            ?: throw (NullPointerException("Employee ID is null, JobCardTechnicians can not have a null technician value"))

        return ResponseJobCardTechnicians(
            jobCardId = jobCard.jobId!!,
            technicianId = technician.employeeId!!
        )
    }

}