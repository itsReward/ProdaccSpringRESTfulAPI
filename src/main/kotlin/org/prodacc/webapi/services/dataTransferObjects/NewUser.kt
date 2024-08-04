package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class NewUser(
    var username: String,
    var password: String,
    var email: String,
    var userRole: String,
    var employeeId: UUID? = null
)

data class UpdateUser(
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var userRole: String? = null,
    var employeeId: UUID? = null
)