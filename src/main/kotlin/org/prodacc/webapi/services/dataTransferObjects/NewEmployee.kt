package org.prodacc.webapi.services.dataTransferObjects

data class NewEmployee(
    var employeeName: String? = null,
    var employeeSurname: String? = null,
    var rating: Float? = null,
    var employeeRole: String? = null,
    var employeeDepartment: String? = null,
    var phoneNumber: String? = null,
    var homeAddress: String? = null
)