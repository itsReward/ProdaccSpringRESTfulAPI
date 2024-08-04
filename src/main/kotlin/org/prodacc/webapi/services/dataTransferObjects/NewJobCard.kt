package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewJobCard(
    var vehicleId: UUID?,
    var serviceAdvisorId: UUID?,
    var supervisorId: UUID?,
    var dateAndTimeIn: LocalDateTime? = null,
    var jobCardStatus: String? = null,
    var estimatedTimeOfCompletion: LocalDateTime? = null,
    var dateAndTimeFrozen: LocalDateTime? = null,
    var dateAndTimeClosed: LocalDateTime? = null,
    var priority: Boolean? = null,
    var jobCardDeadline: LocalDateTime? = null
)
