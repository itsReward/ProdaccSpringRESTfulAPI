package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.EmployeeService
import org.prodacc.webapi.services.dataTransferObjects.NewEmployee
import org.prodacc.webapi.services.dataTransferObjects.ResponseEmployeeWithJobCards
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/employees")
class EmployeeController(
    private val employeeService: EmployeeService
) {

    @GetMapping("/all")
    fun getEmployees (): Iterable<ResponseEmployeeWithJobCards> = employeeService.getEmployees()


    @GetMapping("/get/{id}")
    fun getEmployeeById(@PathVariable id: UUID): ResponseEmployeeWithJobCards = employeeService.getEmployeeById(id)


    @PostMapping("/new")
    fun createNewEmployee(@RequestBody employee: NewEmployee): ResponseEmployeeWithJobCards =
        employeeService.createNewEmployee(employee)


    @PutMapping("/update/{id}")
    fun updateEmployee(@PathVariable id: UUID, @RequestBody updatedEmployee: NewEmployee): ResponseEmployeeWithJobCards =
        employeeService.updateEmployee(id, updatedEmployee)


    @DeleteMapping("/delete/{id}")
    fun deleteEmployee(@PathVariable id: UUID) :ResponseEntity<String> = employeeService.deleteEmployee(id)
}