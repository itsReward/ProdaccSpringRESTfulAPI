package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardStatus
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.JobCardStatusRepository
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobCardStatusService (
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardRepository: JobCardRepository
){
    fun addJobCardStatus(newJobCardStatus: NewJobCardStatus): JobCardStatus {
        return jobCardStatusRepository.save(newJobCardStatus.toJobCardStatus())
    }

    fun getAllJobCardStatusRecords(jobCardId: UUID): List<JobCardStatus> {
        val jobCard = jobCardRepository.findById(jobCardId).orElseThrow { EntityNotFoundException("JobCard not found with id $jobCardId") }
        return jobCardStatusRepository.getJobCardStatusByJobCardUUID(jobCard)
    }


    fun NewJobCardStatus.toJobCardStatus(): JobCardStatus {
        val jobCard = jobCardRepository.findById(this.jobCard).orElseThrow { throw EntityNotFoundException("JobCard not found with id ${this.jobCard}") }
        return JobCardStatus(
            jobCardUUID =jobCard,
            status = this.status,
            createdAt = this.createdAt,
        )
    }
}