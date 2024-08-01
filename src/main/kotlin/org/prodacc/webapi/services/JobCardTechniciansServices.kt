package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.JobCardTechnicians
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.JobCardTechniciansRepository
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardTechnicians
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobCardTechniciansServices(
    private val jobCardTechniciansRepository: JobCardTechniciansRepository,
    private val employeeRepository: EmployeeRepository,
    private val jobCardRepository: JobCardRepository,
) {
    fun addJobCardTechnician(jobCardId: UUID, technicianId: UUID): ResponseJobCardTechnicians {
        val technician = employeeRepository.findById(technicianId)
            .orElseThrow { EntityNotFoundException("Technician with id $technicianId Not Found") }
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job Card with id $jobCardId Not Found") }
        return jobCardTechniciansRepository.save(JobCardTechnicians(jobCard, technician)).toResponseJobCardTechnicians()
    }

    fun getJobCardTechniciansByJobCardId(jobCardId: UUID): List<Employee> {
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job Card with id $jobCardId not found") }
        val jobCardWithTechnicians = jobCardTechniciansRepository.getJobCardTechniciansByJobCardId(jobCard)
        val technicians = jobCardWithTechnicians.map {
            employeeRepository.findById(it.employeeId?.let { employee ->
                employee.employeeId ?: throw (IllegalArgumentException("Employee Id can not be null"))
            } ?: throw (IllegalArgumentException("Employee can not be null")))
                .orElseThrow { EntityNotFoundException("Technician Not Found") }
        }
        return technicians
    }

    fun getJobCardsDoneByTechnician(technicianId: UUID): List<JobCard?> {
        val technician =
            employeeRepository.findById(technicianId).orElseThrow { EntityNotFoundException("Technician Not Found") }
        val jobCardWithTechnicians = jobCardTechniciansRepository.getJobCardTechniciansByEmployeeId(technician)
        val jobCards = jobCardWithTechnicians.map {
            jobCardRepository.findById(it.jobCardId?.let { jobCard ->
                jobCard.job_id ?: throw (IllegalArgumentException("Job Card Not Found"))
            } ?: throw (IllegalArgumentException("Job Card with id $it.job_id Not Found")))
                .orElseThrow { EntityNotFoundException("Job Cards Not Found") }
        }


        return jobCards
    }

    fun deleteJobCardTechniciansByEmployee(technicianId: UUID): ResponseEntity<String> {
        val technician = employeeRepository.findById(technicianId)
            .orElseThrow { EntityNotFoundException("Technician with is $technicianId not found ") }
        try {
            jobCardTechniciansRepository.deleteJobCardTechniciansByEmployeeId(technician)
            return ResponseEntity("Technician Removed", HttpStatus.OK)
        } catch (_: EntityNotFoundException) {
            return ResponseEntity("Technician Not Found", HttpStatus.NOT_FOUND)
        }
    }

    fun JobCardTechnicians.toResponseJobCardTechnicians(): ResponseJobCardTechnicians {
        val jobCard = this.jobCardId?.let {
            jobCardRepository.findById(it.job_id ?: throw (IllegalArgumentException("Job ID is null")))
                .orElseThrow { EntityNotFoundException("JobCard with id ${it.job_id} not found") }
        }
            ?: throw (NullPointerException("Job Card returned null, JobCardTechnicians can not have a null jobcard value"))

        val technician = this.employeeId?.let {
            employeeRepository.findById(it.employeeId ?: throw (IllegalArgumentException("Employee ID is null")))
                .orElseThrow { EntityNotFoundException("Employee ID is null") }
        }
            ?: throw (NullPointerException("Employee ID is null, JobCardTechnicians can not have a null technician value"))

        return ResponseJobCardTechnicians(
            jobCardId = jobCard.job_id!!,
            technicians = technician.employeeId!!
        )
    }

}