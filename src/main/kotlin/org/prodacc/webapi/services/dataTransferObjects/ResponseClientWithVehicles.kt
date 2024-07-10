package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class ResponseClientWithVehicles(
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
