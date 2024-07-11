package org.prodacc.webapi.services

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.User
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.UserRepository
import org.prodacc.webapi.services.dataTransferObjects.NewUser
import org.prodacc.webapi.services.dataTransferObjects.ResponseUserWithEmployee
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class UserService(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun getAllUsers(): List<ResponseUserWithEmployee> {
        log.info("getting all users")
        return userRepository.findAll().map { user -> user.toViewUserWithEmployee() }
    }

    fun getUserById(id: UUID): ResponseUserWithEmployee {
        log.info("getting user with id: $id")
        return userRepository.findById(id)
            .map { it.toViewUserWithEmployee() }
            .orElseThrow { EntityNotFoundException("User with id: $id not found") }
    }

    fun findUserByUsername(username: String): ResponseUserWithEmployee {
        log.info("getting user by username: $username")
        return userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User with username: $username not found") }
            .toViewUserWithEmployee()
    }

    fun findUserByEmail(email: String): ResponseUserWithEmployee {
        log.info("getting user by email: $email")
        return userRepository.findByEmail(email)
            .orElseThrow { EntityNotFoundException("User with email: $email") }
            .toViewUserWithEmployee()
    }

    fun findUserByEmployeeId(id: UUID): ResponseUserWithEmployee {
        log.info("getting user by employee id: $id")
        val employee = employeeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Employee with id: $id not found") }
        return userRepository.findUserByEmployeeId(employee)
            .orElseThrow { EntityNotFoundException("User associated with employee: $id not found") }
            .toViewUserWithEmployee()
    }

    @Transactional
    fun createUser(user: NewUser): ResponseUserWithEmployee {
        log.info("creating user")
        val employee = user.employeeId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee with id: $it not found") }
        }

        return if (userRepository.findByUsername(user.username).isPresent) {
            throw EntityExistsException("User with username ${user.username} exists")
        } else if (userRepository.findByEmail(user.email).isPresent) {
            throw EntityExistsException("User with email ${user.email} exists")
        } else if (employee?.let { userRepository.findUserByEmployeeId(it).isPresent } == true) {
            throw EntityExistsException("User with employee id ${employee.employeeId} exists")
        } else {
            userRepository.save(
                User(
                    username = user.username,
                    password = user.password,
                    email = user.email,
                    userRole = user.userRole,
                    employeeId = employee,
                )
            )
        }.toViewUserWithEmployee()

    }


    @Transactional
    fun updateUser(id: UUID, user: NewUser): ResponseUserWithEmployee {
        log.info("updating user with id: $id")
        val oldUser = userRepository.findById(id).orElseThrow { EntityNotFoundException("User with id: $id not found") }
        val employee = user.employeeId?.let { employeeRepository.findById(it) }?.get()
        val updatedUser = oldUser.copy(
            username = user.username,
            password = user.password,
            email = user.email,
            userRole = user.userRole,
            employeeId = employee
        )


        return userRepository.save(updatedUser).toViewUserWithEmployee()
    }

    @Transactional
    fun deleteUser(id: UUID): ResponseEntity<String> {
        log.info("deleting user with id: $id")
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            ResponseEntity("User deleted", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("User with id: $id not found")
        }
    }


    private fun User.toViewUserWithEmployee(): ResponseUserWithEmployee {
        val employee = this.employeeId
        return ResponseUserWithEmployee(
            id = this.id!!,
            username = this.username!!,
            email = this.email!!,
            userRole = this.userRole!!,
            employeeId = employee?.employeeId,
            employeeName = employee?.employeeName,
            employeeSurname = employee?.employeeSurname
        )
    }

}