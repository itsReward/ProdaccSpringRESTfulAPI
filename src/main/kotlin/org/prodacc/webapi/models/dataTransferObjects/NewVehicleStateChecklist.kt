package org.prodacc.webapi.models.dataTransferObjects

import java.time.LocalDateTime
import java.util.UUID

data class NewVehicleStateChecklist(
    var jobCardId: UUID,
    var millageIn: String,
    var millageOut: String,
    var fuelLevelIn: String,
    var fuelLevelOut: String,
    var created: LocalDateTime,
    var checklist: MutableMap<String , Any>
)
