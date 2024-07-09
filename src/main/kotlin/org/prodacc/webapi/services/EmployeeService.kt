package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.dataTransferObjects.EmployeeWithJobCardIdAndName
import org.prodacc.webapi.models.dataTransferObjects.JobCardWithIdAndName
import org.prodacc.webapi.models.dataTransferObjects.NewEmployee
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val jobCardRepository: JobCardRepository
) {
    private val logger = LoggerFactory.getLogger(EmployeeService::class.java)

    fun getEmployees (): Iterable<EmployeeWithJobCardIdAndName> {
        logger.info("Fetching all employees")
        return employeeRepository.findAll().map { it.toEmployeeWithJobCardIdAndName() }
    }


    fun getEmployeeById( id: UUID): EmployeeWithJobCardIdAndName {
        logger.info("Fetching employee by id: $id")
        return employeeRepository
            .findById(id)
            .map { it.toEmployeeWithJobCardIdAndName()}
            .orElseThrow{EntityNotFoundException("Employee with id $id not found")}
    }


    @Transactional
    fun createNewEmployee(employee: NewEmployee): EmployeeWithJobCardIdAndName {
        logger.info("Creating new employee")
        return employeeRepository.save(employee.toEmployee()).toEmployeeWithJobCardIdAndName()
    }


    @Transactional
    fun updateEmployee( id: UUID, updatedEmployee: NewEmployee): EmployeeWithJobCardIdAndName {
        val existingEmployee = employeeRepository.findById(id).orElseThrow { EntityNotFoundException(" Employee with id $id not found") }
        val updated = existingEmployee.copy(
            employeeName = updatedEmployee.employeeName ?: existingEmployee.employeeName,
            employeeSurname = updatedEmployee.employeeSurname ?: existingEmployee.employeeSurname,
            rating = updatedEmployee.rating ?: existingEmployee.rating,
            employeeRole = updatedEmployee.employeeRole ?: existingEmployee.employeeRole,
            employeeDepartment = updatedEmployee.employeeDepartment ?: existingEmployee.employeeDepartment,
            phoneNumber = updatedEmployee.phoneNumber ?: existingEmployee.phoneNumber,
            homeAddress = updatedEmployee.homeAddress ?: existingEmployee.homeAddress
        )
        return employeeRepository.save(updated).toEmployeeWithJobCardIdAndName()
    }


    @Transactional
    fun deleteEmployee(id: UUID) : ResponseEntity<String> {
        return if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id)
            ResponseEntity("Employee deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Employee with id $id not found")
        }
    }

    private fun Employee.toEmployeeWithJobCardIdAndName(): EmployeeWithJobCardIdAndName {
        val jobCards =  if (this.employeeRole == "Service Advisor") {
            jobCardRepository.getJobCardsByServiceAdvisor(this).map { it.toJobCardWithIdAndName() }
        } else if (this.employeeRole == "technician") {
            jobCardRepository.getJobCardsByTechnician(this).map { it.toJobCardWithIdAndName() }
        } else if (this.employeeRole == "Supervisor") {
            jobCardRepository.getJobCardsBySupervisor(this).map { it.toJobCardWithIdAndName() }
        } else {
            listOf()
        }

        return EmployeeWithJobCardIdAndName(
            id = this.employeeId!!,
            employeeName = this.employeeName,
            employeeSurname = this.employeeSurname,
            rating = this.rating,
            employeeRole = this.employeeRole,
            employeeDepartment = this.employeeDepartment,
            phoneNumber = this.phoneNumber,
            homeAddress = this.homeAddress,
            jobCards = jobCards
        )
    }

    private fun JobCard.toJobCardWithIdAndName(): JobCardWithIdAndName {
        return JobCardWithIdAndName(
            id = this.job_id!!,
            name = this.jobCardName!!
        )
    }

    private fun NewEmployee.toEmployee(): Employee {
        return Employee(
            employeeName = this.employeeName?: throw NullPointerException("Employee name cannot be empty"),
            employeeSurname = this.employeeSurname?: throw NullPointerException("Employee surname cannot be empty"),
            employeeRole = this.employeeRole?: throw NullPointerException("Employee role cannot be empty"),
            employeeDepartment = this.employeeDepartment?: throw NullPointerException("Employee department cannot be empty"),
            phoneNumber = this.phoneNumber?: throw NullPointerException("Phone number cannot be empty"),
            homeAddress = this.homeAddress?: throw NullPointerException("Home address cannot be empty"),
            rating = this.rating?: 0f
        )
    }


}