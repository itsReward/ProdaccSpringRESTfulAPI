package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.ProductVehicle
import java.util.*

data class CreateProductVehicle(
    val vehicleMake: String,
    val vehicleModel: String,
    val year : Int
)

data class ProductVehicleResponseDto(
    val id: UUID,
    val vehicleMake: String,
    val vehicleModel: String?,
    val year: Int
)

fun ProductVehicle.toDto(): ProductVehicleResponseDto = ProductVehicleResponseDto(
    id = this.id!!,
    vehicleMake = this.vehicleMake,
    vehicleModel = this.vehicleModel,
    year = this.year,
)
