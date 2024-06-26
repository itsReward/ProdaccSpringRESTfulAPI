package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Employees
import org.prodacc.webapi.repositories.EmployeeRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class EmployeeController(
    private val employeeRepository: EmployeeRepository
) {
    @GetMapping("/employees")
    fun getEmployees (): Iterable<Employees> = employeeRepository.findAll()

    @GetMapping("/employees/{id}")
    fun getEmployeeById(id: UUID): Optional<Employees> = employeeRepository.findById(id)

    @PostMapping("/employees/new")
    fun createNewEmployee(employee: Employees): Employees = employeeRepository.save(employee)
}