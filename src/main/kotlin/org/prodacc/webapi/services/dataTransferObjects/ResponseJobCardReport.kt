package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class ResponseJobCardReport(
    val reportId: UUID,
    val jobCardId: UUID,
    val employeeId: UUID,
    var reportType: String,
    var jobReport: String
)
