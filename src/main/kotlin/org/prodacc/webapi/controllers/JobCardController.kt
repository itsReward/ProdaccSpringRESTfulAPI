package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.models.dataTransferObjects.JobCardWithAllEntities
import org.prodacc.webapi.models.dataTransferObjects.NewJobCard
import org.prodacc.webapi.models.dataTransferObjects.ViewJobCard
import org.prodacc.webapi.repositories.*
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.swing.text.View
import kotlin.jvm.optionals.getOrNull


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
    fun getJobCards():Iterable<ViewJobCard>  = jobCardRepository.findAll().map { jobCard ->
        ViewJobCard(
            jobCardName = jobCard.jobCardName!!,
            jobCardNumber = jobCard.jobCardNumber!!,
            vehicleId = vehicleRepository.findById(jobCard.vehicleReference!!.id!!).get().id!!,
            vehicleName = vehicleRepository.findById(jobCard.vehicleReference!!.id!!).get().model!!,
            clientId = clientRepository.findById(jobCard.customerReference!!.id!!).get().id!!,
            clientName = clientRepository.findById(jobCard.customerReference!!.id!!).get().clientName + clientRepository.findById(jobCard.customerReference!!.id!!).get().clientSurname,
            serviceAdvisorId = employeeRepository.findById(jobCard.serviceAdvisor?.employeeId!!).get().employeeId!!,
            serviceAdvisorName = employeeRepository.findById(jobCard.serviceAdvisor?.employeeId!!).get().employeeName + employeeRepository.findById(jobCard.serviceAdvisor?.employeeId!!).get().employeeName,
            serviceAdvisorReport = jobCard.serviceAdvisorReport?: "null",
            supervisorId = employeeRepository.findById(jobCard.supervisor?.employeeId!!).get().employeeId!!,
            supervisorName = employeeRepository.findById(jobCard.supervisor?.employeeId!!).get().employeeName + employeeRepository.findById(jobCard.supervisor?.employeeId!!).get().employeeSurname,
            technicianId = if (jobCard.technician != null) { jobCard.technician!!.employeeId!! }else null,
            technicianName = if (jobCard.technician != null) {
                jobCard.technician!!.employeeName + jobCard.technician!!.employeeSurname
            } else null,
            timesheets = timesheetRepository.getTimesheetsByJobCardUUID(jobCard),
            serviceChecklistId = if (serviceChecklistRepository.getVehicleServiceChecklistByJobCard(jobCard).isPresent) {
                serviceChecklistRepository.getVehicleServiceChecklistByJobCard(jobCard).get().id
            }else null ,
            controlChecklistId = if (controlChecklistRepository.findVehicleControlChecklistByJobCard(jobCard).isPresent) {
                controlChecklistRepository.findVehicleControlChecklistByJobCard(jobCard).get().id
            } else null,
            stateChecklistId = if (vehicleStateRepository.findVehicleStateChecklistByJobCard(jobCard).isPresent) {
                vehicleStateRepository.findVehicleStateChecklistByJobCard(jobCard).get().id
            } else null,
        )
    }



    @GetMapping("/get/{id}")
    fun getJobCard(@PathVariable id: UUID): ResponseEntity<ViewJobCard> {
        val jobCard =  jobCardRepository.findById(id)
        if (jobCard.isPresent) {
            val client = clientRepository.findById(jobCard.get().customerReference!!.id!!)
            val vehicle = vehicleRepository.findById(jobCard.get().vehicleReference!!.id!!)

            val serviceAdvisor = employeeRepository.findById(jobCard.get().serviceAdvisor?.employeeId!!)
            val supervisor = employeeRepository.findById(jobCard.get().supervisor?.employeeId!!)
            val technician = if (jobCard.get().technician != null) {
                val uuid = jobCard.get().technician!!.employeeId!!
                employeeRepository.findById(uuid)
            }else{
                null
            }

            val timesheet = timesheetRepository.getTimesheetsByJobCardUUID(jobCard.get())
            val serviceChecklist = serviceChecklistRepository.getVehicleServiceChecklistByJobCard(jobCard.get())
            val controlChecklist = controlChecklistRepository.findVehicleControlChecklistByJobCard(jobCard.get())
            val stateChecklist = vehicleStateRepository.findVehicleStateChecklistByJobCard(jobCard.get())

            val viewJobCard = ViewJobCard(
                jobCardName = jobCard.get().jobCardName!!,
                jobCardNumber = jobCard.get().jobCardNumber!!,
                vehicleId = vehicle.get().id!!,
                vehicleName = vehicle.get().model!!,
                clientId = client.get().id!!,
                clientName = client.get().clientName + client.get().clientSurname,
                serviceAdvisorId = serviceAdvisor.get().employeeId!!,
                serviceAdvisorName = serviceAdvisor.get().employeeName + serviceAdvisor.get().employeeName,
                serviceAdvisorReport = jobCard.get().serviceAdvisorReport?: "null",
                supervisorId = supervisor.get().employeeId!!,
                supervisorName = supervisor.get().employeeName + supervisor.get().employeeSurname,
                technicianId = if (jobCard.get().technician != null) { jobCard.get().technician!!.employeeId!! }else null,
                technicianName = if (jobCard.get().technician != null) {
                    jobCard.get().technician!!.employeeName + jobCard.get().technician!!.employeeSurname
                } else null,
                timesheets = timesheet,
                serviceChecklistId = if (serviceChecklist.isPresent) serviceChecklist.get().id else null,
                controlChecklistId = if (controlChecklist.isPresent) controlChecklist.get().id else null,
                stateChecklistId = if (stateChecklist.isPresent) stateChecklist.get().id else null,
            )


            return ResponseEntity.ok(viewJobCard)
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }


    @PostMapping("/new")
    fun newJobCard(@RequestBody newJobCard: NewJobCard): ViewJobCard {
        val vehicle = vehicleRepository.findById(newJobCard.vehicleId)
        val client = clientRepository.findClientByVehiclesContaining(vehicle.get())
        val supervisor = employeeRepository.findById(newJobCard.supervisorId)
        val serviceAdvisor = employeeRepository.findById(newJobCard.serviceAdvisorId)
        val technician = newJobCard.technicianId?.let { employeeRepository.findById(it) }

        val jobCard = JobCard(
            vehicleReference = vehicle.get(),
            customerReference = client,
            serviceAdvisor = serviceAdvisor.get(),
            supervisor = supervisor.get(),
            dateAndTimeIn = Instant.now(),
            serviceAdvisorReport = newJobCard.serviceAdvisorReport ?: "",
            jobCardStatus = newJobCard.jobCardStatus,
            estimatedTimeOfCompletion = newJobCard.estimatedTimeOfCompletion?.toInstant(ZoneId.systemDefault().rules.getOffset(newJobCard.estimatedTimeOfCompletion)),
            dateAndTimeFrozen = newJobCard.dateAndTimeFrozen?.toInstant(ZoneId.systemDefault().rules.getOffset(newJobCard.dateAndTimeFrozen)),
            dateAndTimeClosed = newJobCard.dateAndTimeClosed?.toInstant(ZoneId.systemDefault().rules.getOffset(newJobCard.dateAndTimeClosed)),
            jobCardDeadline = newJobCard.jobCardDeadline?.toInstant(ZoneId.systemDefault().rules.getOffset(newJobCard.jobCardDeadline)),
            technician = if (technician?.isPresent == true) technician.get() else null,
            priority = newJobCard.priority,
            workDone = newJobCard.workDone,
            additionalWorkDone = newJobCard.additionalWorkDone,
            jobCardName = "${client.clientName.first()} ${client.clientSurname}'s ${vehicle.get().model}",
            jobCardNumber = (1..1989809802).random()

        )

        val savedJobCard = jobCardRepository.save(jobCard)

        val timesheet = timesheetRepository.getTimesheetsByJobCardUUID(savedJobCard)
        val serviceChecklist = serviceChecklistRepository.getVehicleServiceChecklistByJobCard(savedJobCard)
        val controlChecklist = controlChecklistRepository.findVehicleControlChecklistByJobCard(savedJobCard)
        val stateChecklist = vehicleStateRepository.findVehicleStateChecklistByJobCard(savedJobCard)
        return ViewJobCard(
            jobCardName = savedJobCard.jobCardName!!,
            jobCardNumber = savedJobCard.jobCardNumber!!,
            vehicleId = vehicle.get().id!!,
            vehicleName = vehicle.get().model!!,
            clientId = client.id!!,
            clientName = client.clientName + client.clientSurname,
            serviceAdvisorId = serviceAdvisor.get().employeeId!!,
            serviceAdvisorName = serviceAdvisor.get().employeeName + serviceAdvisor.get().employeeSurname,
            serviceAdvisorReport = savedJobCard.serviceAdvisorReport ?: "",
            supervisorId = supervisor.get().employeeId!!,
            supervisorName = supervisor.get().employeeName + supervisor.get().employeeSurname,
            timesheets = timesheet,
            serviceChecklistId = if (serviceChecklist.isPresent) serviceChecklist.get().id else null,
            controlChecklistId = if (controlChecklist.isPresent) controlChecklist.get().id else null,
            stateChecklistId = if (stateChecklist.isPresent) stateChecklist.get().id else null,
        )
    }

    @DeleteMapping("/delete/{jobCardId}")
    fun deleteJobCard(@PathVariable jobCardId: UUID): ResponseEntity<String> {
        return if (jobCardRepository.existsById(jobCardId)) {
            jobCardRepository.deleteById(jobCardId)
            ResponseEntity("JobCard deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("JobCard not found", HttpStatus.NOT_FOUND)

        }
    }

}