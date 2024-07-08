package org.prodacc.webapi.models.dataTransferObjects

import java.util.*

data class ViewUserWithEmployee(
    var id: UUID,
    var username: String,
    var email: String,
    val userRole: String,
    var employeeId: UUID?,
    var employeeName: String?,
    var employeeSurname: String?
)
