package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.Timesheet
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface TimesheetRepository: CrudRepository<Timesheet, UUID> {
    fun getTimesheetsByJobCardUUID(jobCardUUID: JobCard): List<Timesheet>
}