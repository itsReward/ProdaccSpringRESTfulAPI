package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.UserService
import org.prodacc.webapi.services.dataTransferObjects.NewUser
import org.prodacc.webapi.services.dataTransferObjects.ResponseUserWithEmployee
import org.prodacc.webapi.services.dataTransferObjects.UpdateUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/all")
    fun getAllUsers() : List<ResponseUserWithEmployee> = userService.getAllUsers()

    @GetMapping("/get/{id}")
    fun getUserById(@PathVariable id : UUID) : ResponseUserWithEmployee = userService.getUserById(id)

    @GetMapping("/find/{username}")
    fun findUserByUsername(@PathVariable username: String): ResponseUserWithEmployee =
        userService.findUserByUsername(username)

    @GetMapping("/find/{email}")
    fun findUserByEmail(@PathVariable email: String): ResponseUserWithEmployee =
        userService.findUserByEmail(email)

    @GetMapping("/find/{employeeId}")
    fun findUserByEmployeeId(@PathVariable employeeId: UUID): ResponseUserWithEmployee =
        userService.findUserByEmployeeId(employeeId)

    @PostMapping("/new")
    fun createUser(@RequestBody user : NewUser) : ResponseUserWithEmployee = userService.createUser(user)

    @PutMapping("/update/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user : UpdateUser) : ResponseUserWithEmployee = userService.updateUser(id, user)

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> = userService.deleteUser(id)
}