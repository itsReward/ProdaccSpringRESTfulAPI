package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class ResponseUserWithEmployee(
    var id: UUID,
    var username: String,
    var email: String,
    val userRole: String,
    var employeeId: UUID?,
    var employeeName: String?,
    var employeeSurname: String?
)
