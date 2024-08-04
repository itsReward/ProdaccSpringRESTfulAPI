package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardStatus
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.JobCardStatusRepository
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardStatus
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobCardStatusService(
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardRepository: JobCardRepository
) {
    fun addJobCardStatus(newJobCardStatus: NewJobCardStatus): ResponseJobCardStatus {
        return jobCardStatusRepository.save(newJobCardStatus.toJobCardStatus()).toResponseJobCardStatus()
    }

    fun getAllJobCardStatusRecords(jobCardId: UUID): List<ResponseJobCardStatus> {
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("JobCard not found with id $jobCardId") }
        return jobCardStatusRepository.getJobCardStatusByJobCardUUID(jobCard).map { it.toResponseJobCardStatus() }
    }


    fun NewJobCardStatus.toJobCardStatus(): JobCardStatus {
        val jobCard = jobCardRepository.findById(this.jobCard)
            .orElseThrow { throw EntityNotFoundException("JobCard not found with id ${this.jobCard}") }
        return JobCardStatus(
            jobCardUUID = jobCard,
            status = this.status,
            createdAt = this.createdAt,
        )
    }

    fun JobCardStatus.toResponseJobCardStatus(): ResponseJobCardStatus {
        val jobCardId = jobCardRepository.findById(
            this.jobCardStatusId ?: throw (IllegalArgumentException("JobCard id can not be null"))
        ).orElseThrow { EntityNotFoundException("JobCard Not Found") }.job_id
            ?: throw (IllegalArgumentException("id can not be null"))

        return ResponseJobCardStatus(
            jobId = jobCardId,
            createdAt = this.createdAt,
            statusId = this.jobCardStatusId ?: throw (IllegalArgumentException("job cards status id can not be null")),
            status = this.status ?: throw (IllegalArgumentException("status can not be null"))
        )
    }
}