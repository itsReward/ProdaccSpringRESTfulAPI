package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Timesheet
import org.prodacc.webapi.models.dataTransferObjects.NewTimesheet
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.TimesheetRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.*


@Controller
@RequestMapping("/api/v1/timesheets")
class TimesheetController (
    private val timesheetRepository: TimesheetRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {

    @GetMapping("/all")
    fun getAllTimesheets(): Iterable<Timesheet> = timesheetRepository.findAll()

    @GetMapping("/timesheet/jobCard/{id}")
    fun getTimesheetByJobCardId(@PathVariable id: UUID): Timesheet? = timesheetRepository.findByIdOrNull(id)

    @GetMapping("/get/{id}")
    fun getTimesheetById(@PathVariable id: UUID): Optional<Timesheet> = timesheetRepository.findById(id)

    @PostMapping("/add")
    fun addTimesheet(@RequestBody timesheet: NewTimesheet): Timesheet? {
        if (jobCardRepository.findById(timesheet.jobCardId).isPresent && employeeRepository.findById(timesheet.employeeId).isPresent) {
            return timesheetRepository.save(
                Timesheet(
                    jobCardUUID = jobCardRepository.findById(timesheet.jobCardId).get(),
                    employee = employeeRepository.findById(timesheet.employeeId).get(),
                    clockInDateAndTime = timesheet.clockInDateAndTime.toInstant(
                        ZoneId.systemDefault().rules.getOffset(
                            timesheet.clockInDateAndTime
                        )
                    ),
                    clockOutDateAndTime = timesheet.clockOutDateAndTime.toInstant(
                        ZoneId.systemDefault().rules.getOffset(
                            timesheet.clockOutDateAndTime
                        )
                    ),
                    sheetTitle = timesheet.sheetTitle,
                    report = timesheet.report
                )
            )

        } else {
            return null
        }
    }



    @DeleteMapping("/{id}")
    fun deleteTimesheet(@PathVariable id: UUID) {
        timesheetRepository.deleteById(id)
    }


}