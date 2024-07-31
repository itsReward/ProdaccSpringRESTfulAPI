package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCardReports
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardReportsRepository: CrudRepository<JobCardReports, UUID> {
}