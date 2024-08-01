package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewJobCardStatus(
    val jobCard: UUID,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
