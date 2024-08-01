package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.JobCardTechnicians
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardTechniciansRepository : CrudRepository<JobCardTechnicians, UUID> {
    fun getJobCardTechniciansByJobCardId(jobCardId: JobCard): List<JobCardTechnicians>
    fun getJobCardTechniciansByEmployeeId(employeeId: Employee): List<JobCardTechnicians>
    fun deleteJobCardTechniciansByEmployeeId(employeeId: Employee)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : JobCardTechnicians?> save(entity: S & Any): S & Any
}