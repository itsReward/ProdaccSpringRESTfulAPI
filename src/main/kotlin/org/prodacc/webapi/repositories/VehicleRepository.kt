package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Vehicle
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface VehicleRepository : CrudRepository<Vehicle, UUID> {
}