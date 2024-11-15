package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Vehicle
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface VehicleRepository : CrudRepository<Vehicle, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : Vehicle?> save(entity: S & Any): S & Any
}