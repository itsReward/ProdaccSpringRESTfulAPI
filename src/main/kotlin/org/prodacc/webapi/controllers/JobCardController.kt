package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.VehicleRepository
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
    //private val timesheetRepository: TimesheetRepository,
    //private val serviceChecklistRepository: VehicleServiceRepository,
    //private val controlChecklistRepository: VehicleControlChecklist,
    //private val vehicleStateRepository: VehicleStateRepository
) {

    @GetMapping("/all")
    fun getJobCards():Iterable<JobCard> = jobCardRepository.findAll()
}