package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleStateChecklist
import org.springframework.data.repository.CrudRepository
import java.util.*

interface StateChecklistRepository: CrudRepository<VehicleStateChecklist, UUID> {
    fun findVehicleStateChecklistByJobCard(jobCard: JobCard): Optional<VehicleStateChecklist>
}