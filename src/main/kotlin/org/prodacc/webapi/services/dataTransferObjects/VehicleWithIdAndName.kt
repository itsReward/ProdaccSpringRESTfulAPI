package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class VehicleWithIdAndName(
    var id: UUID? = null,
    var model: String? = null,
    var make: String? = null
)
