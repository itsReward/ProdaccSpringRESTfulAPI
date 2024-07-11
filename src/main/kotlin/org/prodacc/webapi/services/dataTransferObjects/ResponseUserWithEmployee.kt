package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

/**
 *Response Entity that returns User with associated employee
 * @since   1.0
 */
data class ResponseUserWithEmployee(
    var id: UUID,
    var username: String,
    var email: String,
    val userRole: String,
    var employeeId: UUID?,
    var employeeName: String?,
    var employeeSurname: String?
)
