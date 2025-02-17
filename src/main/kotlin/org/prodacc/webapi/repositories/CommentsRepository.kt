package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.JobCardComments
import org.prodacc.webapi.services.dataTransferObjects.Comment
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CommentsRepository : CrudRepository<JobCardComments, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCardComments?> save(entity: S & Any): S & Any

    fun getCommentsByJobCardId(jobCardId: JobCard): List<JobCardComments>
    fun deleteJobCardCommentsByJobCardId(jobCardId: JobCard)
}