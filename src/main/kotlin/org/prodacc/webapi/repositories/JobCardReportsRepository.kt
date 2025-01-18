package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.JobCardReports
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardReportsRepository: CrudRepository<JobCardReports, UUID> {
    fun getJobCardReportsByJobCardId(jobCardId: JobCard): List<JobCardReports>
    fun deleteJobCardReportsByJobCardId(jobCardId: JobCard)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCardReports?> save(entity: S & Any): S & Any
}