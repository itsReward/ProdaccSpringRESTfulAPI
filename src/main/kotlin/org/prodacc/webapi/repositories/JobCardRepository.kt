package org.prodacc.webapi.repositories;

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*


interface JobCardRepository: CrudRepository<JobCard, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCard?> save(entity: S & Any): S & Any
}
