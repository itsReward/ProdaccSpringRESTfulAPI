package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.TimeSheetService
import org.prodacc.webapi.services.dataTransferObjects.ResponseTimesheetWithJobCard
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*


@Controller
@RequestMapping("/timesheets")
class TimesheetController (
    private val timeSheetService: TimeSheetService
) {

    @GetMapping("/all")
    fun getAllTimesheets(): Iterable<ResponseTimesheetWithJobCard> = timeSheetService.getAllTimesheets()

    @GetMapping("/jobCard/{id}")
    fun getTimesheetByJobCardId(@PathVariable id: UUID): Iterable<ResponseTimesheetWithJobCard?> = timeSheetService.getTimesheetByJobCardId(id)

    @GetMapping("/get/{id}")
    fun getTimesheetById(@PathVariable id: UUID): ResponseTimesheetWithJobCard = timeSheetService.getTimesheetById(id)

    @PostMapping("/add")
    fun addTimesheet(@RequestBody timesheet: org.prodacc.webapi.services.dataTransferObjects.NewTimesheet): ResponseTimesheetWithJobCard = timeSheetService.addTimesheet(timesheet)

    @PutMapping("/update/{id}")
    fun updateTimesheet(@PathVariable id: UUID, @RequestBody newTimesheet: org.prodacc.webapi.services.dataTransferObjects.NewTimesheet): ResponseTimesheetWithJobCard =
        timeSheetService.updateTimesheet(id, newTimesheet)


    @DeleteMapping("/delete/{id}")
    fun deleteTimesheet(@PathVariable id: UUID) : ResponseEntity<String> = timeSheetService.deleteTimesheet(id)


}