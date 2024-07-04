package org.prodacc.webapi.models.dataTransferObjects

import org.prodacc.webapi.models.Timesheet
import java.time.LocalDateTime
import java.util.*

class ViewJobCard (
    var jobCardName: String,
    var jobCardNumber: Int,
    var vehicleId: UUID,
    var vehicleName: String,
    var clientId: UUID,
    var clientName: String,
    var serviceAdvisorId: UUID,
    var serviceAdvisorName: String,
    var supervisorId: UUID,
    var supervisorName: String,
    var dateAndTimeIn: LocalDateTime? = null,
    var serviceAdvisorReport: String,
    var jobCardStatus: String = "opened",
    var estimatedTimeOfCompletion: LocalDateTime? = null,
    var dateAndTimeFrozen: LocalDateTime? = null,
    var dateAndTimeClosed: LocalDateTime? = null,
    var technicianDiagnosis: String? = null,
    var technicianId: UUID? = null,
    var technicianName: String? = null,
    var priority: Boolean? = null,
    var jobCardDeadline: LocalDateTime? = null,
    var workDone: String? = null,
    var additionalWorkDone: String? = null,
    var timesheets: List<Timesheet> = listOf(),
    var stateChecklistId: UUID? = null,
    var serviceChecklistId: UUID? = null,
    var controlChecklistId: UUID? = null,
)