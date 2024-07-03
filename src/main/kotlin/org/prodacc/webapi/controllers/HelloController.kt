package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.repositories.EmployeeRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HelloController (
    private val employeeRepository: EmployeeRepository
){
    @GetMapping("/hello")
    fun hello(): Iterable<Employee> {
        return employeeRepository.findAll()
    }
}