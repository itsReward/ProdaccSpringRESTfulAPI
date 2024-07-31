package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCardStatus
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardStatusRepository: CrudRepository<JobCardStatus, UUID> {
}