package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class TimesheetWithJobCardIdAndName(
    val id: UUID,
    val sheetTitle: String,
    val report: String,
    val clockInDateAndTime: LocalDateTime?,
    val clockOutDateAndTime: LocalDateTime?,
    val jobCardId: UUID,
    val jobCardName: String?,
    val technicianId: UUID,
    val technicianName: String?,
)
