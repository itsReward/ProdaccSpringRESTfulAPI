package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.dataTransferObjects.UpdateEmployee
import org.prodacc.webapi.repositories.EmployeeRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*


@RestController
@RequestMapping("/api/v1/employees")
class EmployeeController(
    private val employeeRepository: EmployeeRepository
) {

    @GetMapping("/all")
    fun getEmployees (): Iterable<Employee> = employeeRepository.findAll()


    @GetMapping("/get/{id}")
    fun getEmployeeById(@PathVariable id: UUID): Optional<Employee> = employeeRepository.findById(id)


    @PostMapping("/new")
    fun createNewEmployee(@RequestBody employee: Employee): Employee = employeeRepository.save(employee)


    @PutMapping("/update/{id}")
    fun updateEmployee(@PathVariable id: UUID, @RequestBody updatedEmployee: UpdateEmployee): ResponseEntity<Employee> {
        return employeeRepository.findById(id).map { existingEmployee ->
            val updated = existingEmployee.copy(
                employeeName = updatedEmployee.employeeName ?: existingEmployee.employeeName,
                employeeSurname = updatedEmployee.employeeSurname ?: existingEmployee.employeeSurname,
                rating = updatedEmployee.rating ?: existingEmployee.rating,
                employeeRole = updatedEmployee.employeeRole ?: existingEmployee.employeeRole,
                employeeDepartment = updatedEmployee.employeeDepartment ?: existingEmployee.employeeDepartment,
                phoneNumber = updatedEmployee.phoneNumber ?: existingEmployee.phoneNumber,
                homeAddress = updatedEmployee.homeAddress ?: existingEmployee.homeAddress
            )
            ResponseEntity.ok().body(employeeRepository.save(updated))
        }.orElse(ResponseEntity.notFound().build())
    }


    @DeleteMapping("/delete/{id}")
    fun deleteEmployee(@PathVariable id: UUID) :ResponseEntity<String> {
        return if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id)
            ResponseEntity("Employee deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("Employee not found", HttpStatus.NOT_FOUND)

        }
    }
}