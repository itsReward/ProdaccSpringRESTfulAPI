package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.Vehicle
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ClientRepository: CrudRepository<Client, UUID> {
    fun findClientByVehiclesContaining(vehicle: Vehicle): Client

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : Client?> save(entity: S & Any): S & Any
}