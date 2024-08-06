package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.JobCardStatus
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardStatusRepository: CrudRepository<JobCardStatus, UUID> {
    fun getJobCardStatusByJobCardId(jobCard: JobCard): List<JobCardStatus>
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCardStatus?> save(entity: S & Any): S & Any

}