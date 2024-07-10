package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.NewTimesheet
import org.prodacc.webapi.models.dataTransferObjects.TimesheetWithJobCardIdAndName
import org.prodacc.webapi.services.TimeSheetService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*


@Controller
@RequestMapping("/api/v1/timesheets")
class TimesheetController (
    private val timeSheetService: TimeSheetService
) {

    @GetMapping("/all")
    fun getAllTimesheets(): Iterable<TimesheetWithJobCardIdAndName> = timeSheetService.getAllTimesheets()

    @GetMapping("/jobCard/{id}")
    fun getTimesheetByJobCardId(@PathVariable id: UUID): Iterable<TimesheetWithJobCardIdAndName?> = timeSheetService.getTimesheetByJobCardId(id)

    @GetMapping("/get/{id}")
    fun getTimesheetById(@PathVariable id: UUID): TimesheetWithJobCardIdAndName = timeSheetService.getTimesheetById(id)

    @PostMapping("/add")
    fun addTimesheet(@RequestBody timesheet: NewTimesheet): TimesheetWithJobCardIdAndName = timeSheetService.addTimesheet(timesheet)

    @PutMapping("/update/{id}")
    fun updateTimesheet(@PathVariable id: UUID, @RequestBody newTimesheet: NewTimesheet): TimesheetWithJobCardIdAndName =
        timeSheetService.updateTimesheet(id, newTimesheet)


    @DeleteMapping("/delete/{id}")
    fun deleteTimesheet(@PathVariable id: UUID) : ResponseEntity<String> = timeSheetService.deleteTimesheet(id)


}