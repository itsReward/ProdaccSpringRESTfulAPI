package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.User
import org.prodacc.webapi.models.dataTransferObjects.NewUser
import org.prodacc.webapi.models.dataTransferObjects.ViewUserWithEmployee
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository
) {
    @GetMapping("/all")
    fun getAllUsers() : List<ViewUserWithEmployee> {
        return userRepository.findAll().map { user ->
            ViewUserWithEmployee(
                id = user.id!!,
                username = user.username!!,
                email = user.email!!,
                userRole = user.userRole!!,
                employeeId = user.employeeId?.employeeId?.let { employeeRepository.findById(it).get().employeeId }!!,
                employeeName = user.employeeId?.employeeId?.let { employeeRepository.findById(it).get().employeeName }!!,
                employeeSurname = user.employeeId?.employeeId?.let { employeeRepository.findById(it).get().employeeSurname }!!
            )
        }
    }

    @GetMapping("/get/{id}")
    fun getUserById(@PathVariable id : UUID) : Optional<User> {
        return userRepository.findById(id)
    }

    @PostMapping("/new")
    fun createUser(@RequestBody user : NewUser) : ViewUserWithEmployee {
        val newUser = userRepository.save(
            User(
                username = user.username,
                password = user.password,
                email = user.email,
                userRole = user.userRole,
                employeeId = if (user.employeeId != null) employeeRepository.findById(user.employeeId!!).get() else null,
            )
        )
        return ViewUserWithEmployee(
            id = newUser.id!!,
            username = newUser.username!!,
            email = newUser.email!!,
            userRole = newUser.userRole!!,
            employeeId = if (newUser.employeeId != null) {
                newUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeId }!!
            } else null,
            employeeName = if (newUser.employeeId != null) {
                newUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeName }!!
            } else null,
            employeeSurname = if (newUser.employeeId != null) {
                newUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeSurname }!!
            } else null
        )
    }

    @PutMapping("/update/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user : NewUser) : ViewUserWithEmployee {
        val oldUser = userRepository.findById(id)
        val updatedUser =userRepository.save(
            oldUser.get().copy(
                username = user.username,
                password = user.password,
                email = user.email,
                userRole = user.userRole,
                employeeId = if (user.employeeId != null) employeeRepository.findById(user.employeeId!!).get() else null
            )
        )

        return ViewUserWithEmployee(
            id = updatedUser.id!!,
            username = updatedUser.username!!,
            email = updatedUser.email!!,
            userRole = updatedUser.userRole!!,
            employeeId = if (updatedUser.employeeId != null) {
                updatedUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeId }!!
            } else null,
            employeeName = if (updatedUser.employeeId != null) {
                updatedUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeName }!!
            } else null,
            employeeSurname = if (updatedUser.employeeId != null) {
                updatedUser.employeeId!!.employeeId?.let { employeeRepository.findById(it).get().employeeSurname }!!
            } else null
        )
    }

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> {
        return if (userRepository.existsById(id)){
            userRepository.deleteById(id)
            ResponseEntity("User deleted", HttpStatus.OK)
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }
}