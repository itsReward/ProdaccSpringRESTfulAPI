package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class ResponseStateChecklist(
    val id: UUID?,
    val jobCardId: UUID?,
    val jobCardName: String?,
    val millageIn: String?,
    val millageOut: String?,
    val fuelLevelIn: String?,
    val fuelLevelOut: String?,
    val created: LocalDateTime?,
    val checklist: Map<String, Any>?
)