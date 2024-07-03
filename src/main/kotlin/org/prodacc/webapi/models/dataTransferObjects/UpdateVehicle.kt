package org.prodacc.webapi.models.dataTransferObjects

import org.prodacc.webapi.models.Client
import java.util.UUID

data class UpdateVehicle(
    var model : String? = null,
    var regNumber : String? = null,
    var make : String? = null,
    var color : String? = null,
    var chassisNumber : String? = null,
    var clientReference : Client? = null
)
