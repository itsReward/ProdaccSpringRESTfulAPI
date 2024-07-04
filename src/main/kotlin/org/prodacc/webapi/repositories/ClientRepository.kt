package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.Vehicle
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ClientRepository: CrudRepository<Client, UUID> {
    fun findClientByVehiclesContaining(vehicle: Vehicle): Client
}