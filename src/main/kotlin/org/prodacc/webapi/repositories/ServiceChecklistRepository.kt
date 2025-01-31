package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.VehicleServiceChecklist
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface ServiceChecklistRepository: CrudRepository<VehicleServiceChecklist, UUID> {
    fun getVehicleServiceChecklistByJobCard(jobCardUUID: JobCard): Optional<VehicleServiceChecklist>

    @Modifying
    @Query("DELETE FROM VehicleServiceChecklist v WHERE v.jobCard.jobId = :jobCardId")
    fun deleteVehicleServiceChecklistByJobCardId(@Param("jobCardId") jobCardId: UUID)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : VehicleServiceChecklist?> save(entity: S & Any): S & Any

    fun deleteVehicleServiceChecklistByJobCard_JobId(jobCardJobId: UUID): Unit
}