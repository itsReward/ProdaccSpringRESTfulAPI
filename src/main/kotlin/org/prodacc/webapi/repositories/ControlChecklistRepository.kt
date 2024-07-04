package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleControlChecklist
import org.springframework.data.repository.CrudRepository
import java.util.Optional
import java.util.UUID

interface ControlChecklistRepository: CrudRepository<VehicleControlChecklist, UUID> {
    fun findVehicleControlChecklistByJobCard(jobCard: JobCard): Optional<VehicleControlChecklist>
}