package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.UserService
import org.prodacc.webapi.services.dataTransferObjects.NewUser
import org.prodacc.webapi.services.dataTransferObjects.ResponseUserWithEmployee
import org.prodacc.webapi.services.dataTransferObjects.UpdateUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
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

    @GetMapping("/findByUserName/{username}")
    fun findUserByUsername(@PathVariable username: String): ResponseUserWithEmployee =
        userService.findUserByUsername(username)

    /**
     * Gets the currently authenticated user
     * @param authentication The authentication object from Spring Security context
     * @return ResponseUserWithEmployee The current user's information
     */
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseUserWithEmployee =
        userService.getCurrentUser(authentication)

    @GetMapping("/findByEmail/{email}")
    fun findUserByEmail(@PathVariable email: String): ResponseUserWithEmployee =
        userService.findUserByEmail(email)

    @GetMapping("/findByEmployeeId/{employeeId}")
    fun findUserByEmployeeId(@PathVariable employeeId: UUID): ResponseUserWithEmployee =
        userService.findUserByEmployeeId(employeeId)

    @PostMapping("/new")
    fun createUser(@RequestBody user : NewUser) : ResponseUserWithEmployee = userService.createUser(user)

    @PutMapping("/update/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user : UpdateUser) : ResponseUserWithEmployee = userService.updateUser(id, user)

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> = userService.deleteUser(id)
}