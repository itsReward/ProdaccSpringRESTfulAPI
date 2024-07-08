package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleControlChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.Optional
import java.util.UUID

interface ControlChecklistRepository: CrudRepository<VehicleControlChecklist, UUID> {
    fun findVehicleControlChecklistByJobCard(jobCard: JobCard): Optional<VehicleControlChecklist>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleControlChecklist?> save(entity: S & Any): S & Any
}