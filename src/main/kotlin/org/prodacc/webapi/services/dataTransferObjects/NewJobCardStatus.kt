package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewJobCardStatusEntry(
    val jobCardId: UUID,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
