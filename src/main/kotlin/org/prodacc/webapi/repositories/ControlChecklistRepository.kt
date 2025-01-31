package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleControlChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ControlChecklistRepository: CrudRepository<VehicleControlChecklist, UUID> {
    fun findVehicleControlChecklistByJobCard(jobCard: JobCard): Optional<VehicleControlChecklist>
    fun deleteVehicleControlChecklistByJobCard(jobCard: JobCard)

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleControlChecklist?> save(entity: S & Any): S & Any

    fun deleteVehicleControlChecklistByJobCard_JobId(jobCardJobId: UUID): Unit
}