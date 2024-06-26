package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Employees
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface EmployeeRepository: CrudRepository<Employees, UUID> {
}
