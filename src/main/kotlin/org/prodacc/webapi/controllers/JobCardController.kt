package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.repositories.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/jobCards/")
class JobCardController(
    private val jobCardRepository: JobCardRepository,
    private val clientRepository: ClientRepository,
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository,
    private val timesheetRepository: TimesheetRepository,
    private val serviceChecklistRepository: ServiceChecklistRepository,
    private val controlChecklistRepository: ControlChecklistRepository,
    private val vehicleStateRepository: StateChecklistRepository
) {

    @GetMapping("/all")
    fun getJobCards():Iterable<JobCard> = jobCardRepository.findAll()
}