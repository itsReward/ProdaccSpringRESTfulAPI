package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewServiceChecklist(
    val jobCardId: UUID,
    val technicianId: UUID,
    val created: LocalDateTime,
    val checklist: MutableMap<String, Any>,
)

data class UpdateServiceChecklist(
    val jobCardId: UUID?,
    val technicianId: UUID?,
    val created: LocalDateTime?,
    val checklist: MutableMap<String, Any>?,
)