package org.prodacc.webapi.models.dataTransferObjects

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.LocalDateTime
import java.util.UUID

data class NewServiceChecklist(
    val jobCardId: UUID?,
    val technician: UUID?,
    val created: LocalDateTime?,
    val checklist: MutableMap<String, Any>?,
)
