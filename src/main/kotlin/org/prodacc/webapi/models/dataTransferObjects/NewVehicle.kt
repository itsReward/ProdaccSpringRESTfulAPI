package org.prodacc.webapi.models.dataTransferObjects

import java.util.*

data class NewVehicle(
    var model: String? = null,
    var regNumber: String? = null,
    var make: String? = null,
    var color: String? = null,
    var chassisNumber: String? = null,
    var clientReference: UUID? = null
)
