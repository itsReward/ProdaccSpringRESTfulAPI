package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleServiceChecklist
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ServiceChecklistRepository: CrudRepository<VehicleServiceChecklist, UUID> {
    fun getVehicleServiceChecklistByJobCard(jobCardUUID: JobCard): Optional<VehicleServiceChecklist>
}