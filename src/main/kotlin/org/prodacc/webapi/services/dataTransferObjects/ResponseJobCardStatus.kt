package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class ResponseJobCardStatus (
    val statusId : UUID,
    val jobId : UUID,
    val status : String,
    val createdAt : LocalDateTime
)
