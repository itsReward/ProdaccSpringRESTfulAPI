package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.*

data class NewTimesheet (
    var jobCardId: UUID?,
    var employeeId: UUID?,
    var clockInDateAndTime: LocalDateTime?,
    var clockOutDateAndTime: LocalDateTime?,
    var sheetTitle: String?,
    var report: String? = null
)