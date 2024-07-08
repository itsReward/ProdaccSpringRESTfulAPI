package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Employee
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface EmployeeRepository: CrudRepository<Employee, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : Employee?> save(entity: S & Any): S & Any
}