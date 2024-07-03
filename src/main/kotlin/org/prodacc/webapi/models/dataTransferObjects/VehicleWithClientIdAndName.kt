package org.prodacc.webapi.models.dataTransferObjects

import java.util.*

data class VehicleWithClientIdAndName(
    var id: UUID? = null,
    var model: String? = null,
    var regNumber: String? = null,
    var make: String? = null,
    var color: String? = null,
    var chassisNumber: String? = null,
    var clientId: UUID? = null,
    var clientName: String? = null,
    var clientSurname: String? = null,
)
