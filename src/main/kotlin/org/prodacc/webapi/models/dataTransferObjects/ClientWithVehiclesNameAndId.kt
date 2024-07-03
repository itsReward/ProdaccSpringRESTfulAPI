package org.prodacc.webapi.models.dataTransferObjects

import org.prodacc.webapi.models.Vehicle
import java.util.UUID

data class ClientWithVehiclesNameAndId(
    val id: UUID? = null,
    val clientName: String? = null,
    val clientSurname: String? = null,
    val gender: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val vehicles: List<VehicleWithIdAndName> = listOf(),
)
