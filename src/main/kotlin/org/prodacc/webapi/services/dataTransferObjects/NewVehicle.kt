package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class NewVehicle(
    val model : String? = null,
    val regNumber : String? = null,
    val make : String? = null,
    val color : String? = null,
    val chassisNumber : String? = null,
    val clientId : UUID? = null
)
