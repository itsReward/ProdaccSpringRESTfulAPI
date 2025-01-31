package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleStateChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface VehicleStateChecklistRepository: CrudRepository<VehicleStateChecklist, UUID> {
    fun findVehicleStateChecklistByJobCard(jobCard: JobCard): Optional<VehicleStateChecklist>

    fun deleteVehicleStateChecklistByJobCard(jobCard: JobCard)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleStateChecklist?> save(entity: S & Any): S & Any

    @Query("SELECT v FROM VehicleStateChecklist v WHERE v.jobCard.jobId = :jobCardId")
    fun findByJobCardId(jobCardId: UUID): Optional<VehicleStateChecklist>

    @Modifying
    @Query("DELETE FROM VehicleStateChecklist v WHERE v.jobCard.jobId = :jobCardId")
    fun deleteByJobCardId(jobCardId: UUID)

    fun deleteVehicleStateChecklistByJobCard_JobId(jobCardJobId: UUID): Unit
}