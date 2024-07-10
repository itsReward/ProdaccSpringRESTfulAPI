package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class ResponseControlChecklist(
    val id: UUID?,
    val jobCardId: UUID?,
    val jobCardName: String?,
    val technicianId: UUID?,
    val technicianName: String?,
    val created: LocalDateTime?,
    val checklist: Map<String, Any>?
)
