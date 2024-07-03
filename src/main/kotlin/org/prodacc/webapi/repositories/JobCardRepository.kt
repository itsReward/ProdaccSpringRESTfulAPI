package org.prodacc.webapi.repositories;

import org.prodacc.webapi.models.JobCard
import org.springframework.data.repository.CrudRepository
import java.util.*


interface JobCardRepository: CrudRepository<JobCard, UUID> {}
