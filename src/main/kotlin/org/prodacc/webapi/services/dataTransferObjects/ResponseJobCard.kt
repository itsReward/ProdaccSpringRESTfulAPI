package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

class ResponseJobCard (
    var id: UUID,
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
    var estimatedTimeOfCompletion: LocalDateTime? = null,
    var dateAndTimeFrozen: LocalDateTime? = null,
    var dateAndTimeClosed: LocalDateTime? = null,
    var technicianId: List<UUID> = mutableListOf(),
    var technicianName: List<String> = mutableListOf(),
    var priority: Boolean? = null,
    var jobCardDeadline: LocalDateTime? = null,
    var timesheets: List<UUID?> = listOf(),
    var stateChecklistId: UUID? = null,
    var serviceChecklistId: UUID? = null,
    var controlChecklistId: UUID? = null,
)