package org.prodacc.webapi.repositories

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.User
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserRepository: CrudRepository<User, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : User?> save(entity: S & Any): S & Any

    fun findByUsername(username: String?): Optional<User>
    fun findByEmail(email: String?): Optional<User>
    fun findUserByEmployeeId(employeeId: Employee): Optional<User>
}