package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.NewUser
import org.prodacc.webapi.models.dataTransferObjects.ViewUserWithEmployee
import org.prodacc.webapi.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/all")
    fun getAllUsers() : List<ViewUserWithEmployee> = userService.getAllUsers()

    @GetMapping("/get/{id}")
    fun getUserById(@PathVariable id : UUID) : ViewUserWithEmployee = userService.getUserById(id)

    @PostMapping("/new")
    fun createUser(@RequestBody user : NewUser) : ViewUserWithEmployee = userService.createUser(user)

    @PutMapping("/update/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user : NewUser) : ViewUserWithEmployee = userService.updateUser(id, user)

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> = userService.deleteUser(id)
}