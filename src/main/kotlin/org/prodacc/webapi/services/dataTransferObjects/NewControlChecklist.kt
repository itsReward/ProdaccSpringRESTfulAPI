package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewControlChecklist (
    var jobCardId: UUID?,
    var technicianId: UUID?,
    var created: LocalDateTime?,
    var checklist: MutableMap<String, Any>?
)