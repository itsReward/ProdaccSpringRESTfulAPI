package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.EmployeeWithJobCardIdAndName
import org.prodacc.webapi.models.dataTransferObjects.NewEmployee
import org.prodacc.webapi.services.EmployeeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/v1/employees")
class EmployeeController(
    private val employeeService: EmployeeService
) {

    @GetMapping("/all")
    fun getEmployees (): Iterable<EmployeeWithJobCardIdAndName> = employeeService.getEmployees()


    @GetMapping("/get/{id}")
    fun getEmployeeById(@PathVariable id: UUID): EmployeeWithJobCardIdAndName = employeeService.getEmployeeById(id)


    @PostMapping("/new")
    fun createNewEmployee(@RequestBody employee: NewEmployee): EmployeeWithJobCardIdAndName =
        employeeService.createNewEmployee(employee)


    @PutMapping("/update/{id}")
    fun updateEmployee(@PathVariable id: UUID, @RequestBody updatedEmployee: NewEmployee): EmployeeWithJobCardIdAndName =
        employeeService.updateEmployee(id, updatedEmployee)


    @DeleteMapping("/delete/{id}")
    fun deleteEmployee(@PathVariable id: UUID) :ResponseEntity<String> = employeeService.deleteEmployee(id)
}