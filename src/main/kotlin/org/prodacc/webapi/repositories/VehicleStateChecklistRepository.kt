package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleStateChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface VehicleStateChecklistRepository: CrudRepository<VehicleStateChecklist, UUID> {
    fun findVehicleStateChecklistByJobCard(jobCard: JobCard): Optional<VehicleStateChecklist>

    fun deleteVehicleStateChecklistByJobCard(jobCard: JobCard)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleStateChecklist?> save(entity: S & Any): S & Any
}