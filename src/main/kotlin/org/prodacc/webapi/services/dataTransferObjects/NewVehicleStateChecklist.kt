package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewVehicleStateChecklist(
    var jobCardId: UUID?,
    var technicianId: UUID?,
    var millageIn: String?,
    var millageOut: String?,
    var fuelLevelIn: String?,
    var fuelLevelOut: String?,
    var created: LocalDateTime?,
    var checklist: MutableMap<String , Any>?
)
