package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleServiceChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ServiceChecklistRepository: CrudRepository<VehicleServiceChecklist, UUID> {
    fun getVehicleServiceChecklistByJobCard(jobCardUUID: JobCard): Optional<VehicleServiceChecklist>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleServiceChecklist?> save(entity: S & Any): S & Any
}