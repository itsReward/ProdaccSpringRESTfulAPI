package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.Timesheet
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TimesheetRepository: CrudRepository<Timesheet, UUID> {
    fun getTimesheetsByJobCardUUID(jobCard: JobCard): List<Timesheet>

    fun deleteTimesheetByJobCardUUID(jobCard: JobCard)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : Timesheet?> save(entity: S & Any): S & Any
}