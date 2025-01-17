package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class ResponseEmployeeWithJobCards (
    val id: UUID,
    val employeeName: String,
    val employeeSurname: String,
    val rating: Float? = 0F,
    val employeeRole: String,
    val employeeDepartment: String,
    val phoneNumber: String,
    val homeAddress: String,
    val jobCards: List<JobCardWithIdAndName>
)

