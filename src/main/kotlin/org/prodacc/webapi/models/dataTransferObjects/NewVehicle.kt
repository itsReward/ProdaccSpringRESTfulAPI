package org.prodacc.webapi.models.dataTransferObjects

import java.util.UUID

data class NewVehicle(
    val model : String? = null,
    val regNumber : String? = null,
    val make : String? = null,
    val color : String? = null,
    val chassisNumber : String? = null,
    val clientReference : UUID? = null
)
