package org.prodacc.webapi.models.dataTransferObjects

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class NewControlChecklist (
    var jobCardId: UUID,
    var technicianId: UUID,
    var created: LocalDate,
    var checklist: MutableMap<String, Any>
)