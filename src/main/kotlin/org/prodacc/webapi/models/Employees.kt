package org.prodacc.webapi.models


import org.springframework.data.annotation.Id
import java.util.*
import javax.annotation.processing.Generated


@Employees.Entity
data class Employees(
    @Id
    @Generated
    val employeeId: UUID = UUID.randomUUID(),
    val employeeName: String,
) {
    annotation class Entity
}
