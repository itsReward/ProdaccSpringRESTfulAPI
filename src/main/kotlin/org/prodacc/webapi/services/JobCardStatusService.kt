package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCardStatus
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.JobCardStatusRepository
import org.prodacc.webapi.services.dataTransferObjects.NewJobCardStatusEntry
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCardStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobCardStatusService(
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardRepository: JobCardRepository
) {
    private val logger = LoggerFactory.getLogger(JobCardStatusService::class.java)


    fun newJobCardStatus(newStatusEntry: NewJobCardStatusEntry): ResponseJobCardStatus {
        val jobCard = jobCardRepository.findById(newStatusEntry.jobCardId).orElseThrow { EntityNotFoundException("Job Card not found.") }
        val statusEntry = JobCardStatus(
            jobCardId = jobCard,
            status = newStatusEntry.status,
            createdAt = newStatusEntry.createdAt,
        )
        return jobCardStatusRepository.save(jobCardStatusRepository.save(statusEntry)).toResponseJobCardStatus()
    }

    fun getAllJobCardStatusRecords(jobCardId: UUID): List<ResponseJobCardStatus> {
        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("JobCard not found with id $jobCardId") }
        return jobCardStatusRepository.getJobCardStatusByJobCardId(jobCard).map { it.toResponseJobCardStatus() }
    }

    fun getAllEntries() = jobCardStatusRepository.findAll().map { it.toResponseJobCardStatus() }



    fun JobCardStatus.toResponseJobCardStatus(): ResponseJobCardStatus {
        val jobCardId = this.jobCardId

        return jobCardId?.let {
            ResponseJobCardStatus(
                jobId = jobCardId.job_id!!,
                createdAt = this.createdAt,
                statusId = this.jobCardStatusId ?: throw (IllegalArgumentException("job cards status id can not be null")),
                status = this.status ?: throw (IllegalArgumentException("status can not be null"))
            )
        } ?: throw (IllegalArgumentException("job card with id $jobCardId not found"))
    }
}