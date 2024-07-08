package org.prodacc.webapi.models.dataTransferObjects

import java.util.*

data class NewUser(
    var username: String,
    var password: String,
    var email: String,
    var userRole: String,
    var employeeId: UUID? = null
)
