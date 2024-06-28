package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Employee
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface EmployeeRepository: CrudRepository<Employee, UUID> {

}