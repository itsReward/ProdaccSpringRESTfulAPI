package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Timesheet
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.repositories.TimesheetRepository
import org.prodacc.webapi.services.dataTransferObjects.NewTimesheet
import org.prodacc.webapi.services.dataTransferObjects.ResponseTimesheetWithJobCard
import org.prodacc.webapi.services.synchronisation.WebSocketHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@Service
class TimeSheetService(
    private val timesheetRepository: TimesheetRepository,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository,
    private val webSocketHandler: WebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(TimeSheetService::class.java)

    fun getAllTimesheets(): Iterable<ResponseTimesheetWithJobCard> {
        logger.info("Fetching all timesheets")
        return timesheetRepository.findAll().map { it.toTimeSheetWithJobCardIdAndName() }
    }

    fun getTimesheetByJobCardId(id: UUID): Iterable<ResponseTimesheetWithJobCard?> {
        logger.info("Fetching timesheet by job card id: $id")
        val jobCard = jobCardRepository.findById(id).orElseThrow { EntityNotFoundException("Job card not found with id: $id") }
        return timesheetRepository.getTimesheetsByJobCardUUID(jobCard).map { it.toTimeSheetWithJobCardIdAndName() }
    }

    fun getTimesheetById( id: UUID): ResponseTimesheetWithJobCard {
        logger.info("Fetching timesheet by id: $id")
        val timesheet  = timesheetRepository.findById(id).orElseThrow { EntityNotFoundException("Timesheet not found with id: $id") }
        return timesheet.toTimeSheetWithJobCardIdAndName()
    }

    @Transactional
    fun addTimesheet(newTimesheet: NewTimesheet): ResponseTimesheetWithJobCard {
        logger.info("Adding New Timesheet")
        val jobCard = newTimesheet.jobCardId?.let {
            jobCardRepository.findById(it).orElseThrow {EntityNotFoundException("Job card not found with id: ${newTimesheet.jobCardId}")}
        }?: throw NullPointerException("Timesheet has to be associated with JobCard, Enter job card id ")
        val technician = newTimesheet.employeeId?.let {
            employeeRepository.findById(it).orElseThrow { EntityNotFoundException ( "Employee not found with id: ${newTimesheet.employeeId}") }
        }?: throw NullPointerException("Timesheet has to be associated with an Employee, Enter employee id")

        val timesheet = Timesheet(
            jobCardUUID = jobCard,
            employee = technician,
            clockInDateAndTime = newTimesheet.clockInDateAndTime,
            clockOutDateAndTime = newTimesheet.clockOutDateAndTime,
            sheetTitle = newTimesheet.sheetTitle,
            report = newTimesheet.report
        )
        val responseTimesheet = timesheetRepository.save(timesheet).toTimeSheetWithJobCardIdAndName()
        webSocketHandler.broadcastUpdate("NEW_TIMESHEET", responseTimesheet.id)
        return responseTimesheet
    }


    @Transactional
    fun updateTimesheet(id: UUID, newTimesheet: NewTimesheet): ResponseTimesheetWithJobCard {
        logger.info("Updating timesheet with id: $id")
        val oldTimesheet = timesheetRepository.findById(id).orElseThrow { EntityNotFoundException("Timesheet not found with id: $id") }
        val technician = newTimesheet.employeeId?.let {
            employeeRepository.findById(it).orElseThrow { EntityNotFoundException ( "Employee not found with id: ${newTimesheet.employeeId}") }
        }
        val update = oldTimesheet.copy(
            sheetTitle = newTimesheet.sheetTitle,
            report = newTimesheet.report,
            clockInDateAndTime = newTimesheet.clockInDateAndTime,
            clockOutDateAndTime = newTimesheet.clockOutDateAndTime,
            employee = technician?: oldTimesheet.employee
        )
        val responseTimesheet = timesheetRepository.save(update).toTimeSheetWithJobCardIdAndName()
        webSocketHandler.broadcastUpdate("UPDATE_TIMESHEET", responseTimesheet.id)
        return responseTimesheet

    }



    @Transactional
    fun deleteTimesheet(@PathVariable id: UUID): ResponseEntity<String> {
        logger.info("deleting timesheet with id: $id")
        return if ( timesheetRepository.findById(id).isPresent ){
            timesheetRepository.deleteById(id)
            webSocketHandler.broadcastUpdate("DELETE_TIMESHEET", id)
            ResponseEntity("Timesheet has been deleted", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Timesheet not found with id: $id")
        }
    }


    private fun Timesheet.toTimeSheetWithJobCardIdAndName() : ResponseTimesheetWithJobCard {
        val jobCard = this.jobCardUUID?.jobId
        .let {
            if (it != null) {
                jobCardRepository.findById(it)
                    .orElseThrow { EntityNotFoundException("JobCard with Id: ${this.jobCardUUID?.jobId} not found") }
            } else {
                throw NullPointerException("Timesheet must be associated with a JobCard!!, enter jobCard id")
            }
        } ?: throw NullPointerException("Timesheet must be associated with a JobCard!!, enter jobCard id")

        val technician = this.employee?.employeeId.let {
            if (it != null) {
                employeeRepository.findById(it).orElseThrow { EntityNotFoundException("Employee with Id: $it not found") }
            } else {
                throw NullPointerException("Timesheet must be associated with a technician, enter employee id")
            }
        } ?: throw NullPointerException("Timesheet must be associated with a technician!!, enter employee id")

        return ResponseTimesheetWithJobCard(
            id = this.id!!,
            sheetTitle = this.sheetTitle!!,
            report = this.report!!,
            clockInDateAndTime = this.clockInDateAndTime,
            clockOutDateAndTime = this.clockOutDateAndTime,
            jobCardId = jobCard.jobId!!,
            jobCardName = jobCard.jobCardName!!,
            technicianId = technician.employeeId!!,
            technicianName = technician.employeeName + " " + technician.employeeSurname,
        )
    }

}