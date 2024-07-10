package org.prodacc.webapi.models.dataTransferObjects

import java.time.LocalDateTime
import java.util.UUID

data class NewJobCard(
    var vehicleId: UUID?,
    var serviceAdvisorId: UUID?,
    var supervisorId: UUID?,
    var dateAndTimeIn: LocalDateTime? = null,
    var serviceAdvisorReport: String? = null,
    var supervisorReport: String? = null,
    var jobCardStatus: String? = null,
    var estimatedTimeOfCompletion: LocalDateTime? = null,
    var dateAndTimeFrozen: LocalDateTime? = null,
    var dateAndTimeClosed: LocalDateTime? = null,
    var technicianDiagnosticReport: String? = null,
    var technicianId: UUID? = null,
    var priority: Boolean? = null,
    var jobCardDeadline: LocalDateTime? = null,
    var workDone: String? = null,
    var additionalWorkDone: String? = null
)
