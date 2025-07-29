package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ProductVehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductVehicleRepository : JpaRepository<ProductVehicle, UUID> {
    fun findByVehicleMakeIgnoreCase(vehicleMake: String): List<ProductVehicle>
    fun findByVehicleMakeIgnoreCaseAndVehicleModelIgnoreCase(vehicleMake: String, vehicleModel: String): List<ProductVehicle>
    fun findProductVehicleByVehicleMakeIgnoreCaseAndVehicleModelIgnoreCaseAndYear(
        vehicleMake: String, vehicleModel: String, year: Int
    )  : Boolean
}