package org.prodacc.webapi.repositories;

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.JobCard
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*


interface JobCardRepository: CrudRepository<JobCard, UUID> {
    fun getJobCardsBySupervisor(supervisor: Employee) : List<JobCard>
    fun getJobCardsByServiceAdvisor(serviceAdvisor: Employee) : List<JobCard>
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCard?> save(entity: S & Any): S & Any
}
