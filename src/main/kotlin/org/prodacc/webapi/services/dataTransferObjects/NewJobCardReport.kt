package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class NewJobCardReport (
    val jobCardId: UUID,
    val employeeId: UUID,
    var reportType: String,
    var jobReport: String
)

data class UpdateJobCardReport (
    val jobCardId: UUID? = null,
    val employeeId: UUID? = null,
    var reportType: String? = null,
    var jobReport: String? = null
)

