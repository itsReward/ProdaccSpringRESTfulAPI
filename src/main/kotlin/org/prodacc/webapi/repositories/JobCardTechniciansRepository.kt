package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCardTechnicians
import org.springframework.data.repository.CrudRepository
import java.util.*

interface JobCardTechniciansRepository : CrudRepository<JobCardTechnicians, UUID> {
}