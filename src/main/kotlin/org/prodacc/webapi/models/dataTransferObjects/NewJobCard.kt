package org.prodacc.webapi.models.dataTransferObjects

import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.Vehicle
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

data class NewJobCard(
    var vehicleId: UUID,
    var serviceAdvisorId: UUID,
    var supervisorId: UUID,
    var dateAndTimeIn: LocalDateTime? = null,
    var serviceAdvisorReport: String,
    var jobCardStatus: String = "opened",
    var estimatedTimeOfCompletion: LocalDateTime? = null,
    var dateAndTimeFrozen: LocalDateTime? = null,
    var dateAndTimeClosed: LocalDateTime? = null,
    var technicianDiagnosis: String? = null,
    var technicianId: UUID? = null,
    var priority: Boolean? = null,
    var jobCardDeadline: LocalDateTime? = null,
    var workDone: String? = null,
    var additionalWorkDone: String? = null
)
