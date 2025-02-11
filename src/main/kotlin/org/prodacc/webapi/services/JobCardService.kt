package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.repositories.*
import org.prodacc.webapi.services.dataTransferObjects.NewJobCard
import org.prodacc.webapi.services.dataTransferObjects.ResponseJobCard
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@Service
class JobCardService(
    private val jobCardRepository: JobCardRepository,
    private val clientRepository: ClientRepository,
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository,
    private val timesheetRepository: TimesheetRepository,
    private val serviceChecklistRepository: ServiceChecklistRepository,
    private val controlChecklistRepository: ControlChecklistRepository,
    private val vehicleStateRepository: VehicleStateChecklistRepository,
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardReportsRepository: JobCardReportsRepository,
    private val jobCardTechniciansRepository: JobCardTechniciansRepository,
    private val webSocketHandler: org.prodacc.webapi.services.synchronisation.WebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getJobCards(): Iterable<ResponseJobCard> {
        logger.info("Fetching all jobCards")
        try {
            return jobCardRepository.findAll().map { it.toViewJobCard() }
        } catch (e:Exception){
            logger.error(e.message)
            throw e
        }

    }


    fun getJobCard(id: UUID): ResponseJobCard {
        logger.info("Fetching job card with id: $id")
        try {
            return jobCardRepository
                .findById(id)
                .map { it.toViewJobCard() }
                .orElseThrow { EntityNotFoundException("JobCard with Id: $id not found") }
        }catch (e:Exception){
            logger.error(e.message)
            throw e
        }

    }


    @Transactional
    fun newJobCard(@RequestBody newJobCard: NewJobCard): ResponseJobCard {
        logger.info("Creating new job card")
        try {
            val jobCard = newJobCard.toJobCard()
            val savedJobCard = jobCardRepository.save(jobCard)
            val responseJobCard = savedJobCard.toViewJobCard()

            webSocketHandler.broadcastUpdate("NEW_JOB_CARD", responseJobCard.id)
            return  responseJobCard
        } catch (e: Exception){
            logger.error(e.message)
            throw e
        }

    }


    @Transactional
    fun updateJobCard(id: UUID, newJobCard: NewJobCard): ResponseJobCard {
        logger.info("Updating job card with id: $id")
        try {
            val oldJobCard =
                jobCardRepository.findById(id).orElseThrow { EntityNotFoundException("JobCard with id: $id not found") }

            val vehicle = newJobCard.vehicleId?.let {
                vehicleRepository.findById(it).orElseThrow { EntityNotFoundException("Vehicle with id: $id not found") }
            }
            val client = vehicle?.let { clientRepository.findClientByVehiclesContaining(it) }
            val supervisor = newJobCard.supervisorId?.let {
                employeeRepository.findById(it).orElseThrow { EntityNotFoundException("Supervisor with id: $id not found") }
            }
            val serviceAdvisor = newJobCard.serviceAdvisorId?.let {
                employeeRepository.findById(it)
                    .orElseThrow { EntityNotFoundException("Service Advisor with id: $id not found") }
            }

            val jobCardName =
                if (vehicle != null) "${client?.clientName?.first()} ${client?.clientSurname}'s ${vehicle.model}" else null

            val jobCard = oldJobCard.copy(
                vehicleReference = vehicle ?: oldJobCard.vehicleReference,
                customerReference = client ?: oldJobCard.customerReference,
                serviceAdvisor = serviceAdvisor ?: oldJobCard.serviceAdvisor,
                supervisor = supervisor ?: oldJobCard.supervisor,
                dateAndTimeIn = newJobCard.dateAndTimeIn ?: oldJobCard.dateAndTimeIn,
                estimatedTimeOfCompletion = newJobCard.estimatedTimeOfCompletion ?: oldJobCard.estimatedTimeOfCompletion,
                dateAndTimeFrozen = newJobCard.dateAndTimeFrozen ?: oldJobCard.dateAndTimeFrozen,
                dateAndTimeClosed = newJobCard.dateAndTimeClosed ?: oldJobCard.dateAndTimeClosed,
                jobCardDeadline = newJobCard.jobCardDeadline ?: oldJobCard.jobCardDeadline,
                priority = newJobCard.priority ?: oldJobCard.priority,
                jobCardName = jobCardName ?: oldJobCard.jobCardName,
                jobCardNumber = oldJobCard.jobCardNumber
            )

            webSocketHandler.broadcastUpdate("UPDATE_JOB_CARD", jobCard.jobId!!)
            return jobCardRepository.save(jobCard).toViewJobCard()
        } catch (e:Exception){
            logger.error(e.message)
            throw e
        }


    }


    @Transactional
    fun deleteJobCard(jobCardId: UUID): ResponseEntity<String> {
        logger.info("Deleting job card with id: $jobCardId")
        try {
            if (!jobCardRepository.existsById(jobCardId)) {
                throw EntityNotFoundException("JobCard with id: $jobCardId does not exist")
            }

            val jobCard = jobCardRepository.findById(jobCardId).get()

            timesheetRepository.deleteTimesheetByJobCardUUID(jobCard)

            vehicleStateRepository.deleteVehicleStateChecklistByJobCard_JobId(jobCard.jobId!!)
            serviceChecklistRepository.deleteVehicleServiceChecklistByJobCard_JobId(jobCard.jobId!!)
            controlChecklistRepository.deleteVehicleControlChecklistByJobCard_JobId(jobCard.jobId!!)

            // Delete any job card technician assignments
            jobCardTechniciansRepository.deleteJobCardTechniciansByJobCardId(jobCard)

            jobCardReportsRepository.deleteJobCardReportsByJobCardId(jobCard)
            // Delete any job card statuses
            jobCardStatusRepository.deleteJobCardStatusesByJobCardId(jobCard)

            // Finally delete the job card
            jobCardRepository.deleteById(jobCardId)

            webSocketHandler.broadcastUpdate("DELETE_JOB_CARD", jobCardId)
            return ResponseEntity("Job Card Deleted successfully", HttpStatus.OK)

        } catch (e: Exception){
            logger.error(e.message)
            throw e
        }

    }


    private fun NewJobCard.toJobCard(): JobCard {
        val vehicle = this.vehicleId?.let {
            vehicleRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Vehicle with id ${this.vehicleId} not found") }
        } ?: throw NullPointerException("Vehicle can not be null")
        val client = clientRepository.findClientByVehiclesContaining(vehicle)
        val supervisor = this.supervisorId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee with id ${this.supervisorId} not found") }
        } ?: throw NullPointerException("Supervisor can not be null")
        val serviceAdvisor = this.serviceAdvisorId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee with id ${this.serviceAdvisorId} not found") }
        } ?: throw NullPointerException("Service Advisor can not be null")

        return JobCard(
            vehicleReference = vehicle,
            customerReference = client,
            serviceAdvisor = serviceAdvisor,
            supervisor = supervisor,
            dateAndTimeIn = this.dateAndTimeIn,
            estimatedTimeOfCompletion = this.estimatedTimeOfCompletion,
            dateAndTimeFrozen = this.dateAndTimeFrozen,
            dateAndTimeClosed = this.dateAndTimeClosed,
            jobCardDeadline = this.jobCardDeadline,
            priority = this.priority,
            jobCardName = "${client.clientName.first()} ${client.clientSurname}'s ${vehicle.model}",
            jobCardNumber = (1..1989809802).random()

        )
    }

    private fun JobCard.toViewJobCard(): ResponseJobCard {
        val vehicle = this.vehicleReference?.id?.let {
            vehicleRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Vehicle with id: $it does not exist") }
        } ?: throw (NullPointerException("No vehicle associated with JobCard, JOB CARD HAS TO HAVE A VEHICLE"))
        val client = this.customerReference?.id?.let {
            clientRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Client with id: $it does not exist") }
        } ?: throw (NullPointerException("No client associated with JobCard, JOB CARD HAS TO HAVE A CLIENT"))

        val serviceAdvisor = this.serviceAdvisor?.employeeId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee With Id: $it not found") }
        }
            ?: throw (NullPointerException("No Service Advisor Associated with JobCard. JobCard has to have a Service Advisor!"))

        val supervisor = this.supervisor?.employeeId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee With Id: $it not found") }
        } ?: throw (NullPointerException("No supervisor Associated with JobCard. JobCard has to have a supervisor!"))



        val timesheets = timesheetRepository.getTimesheetsByJobCardUUID(this).map { it.id }
        val serviceChecklistId = if (serviceChecklistRepository.getVehicleServiceChecklistByJobCard(this).isPresent) {
            serviceChecklistRepository.getVehicleServiceChecklistByJobCard(this).get().id
        } else null
        val controlChecklistId = if (controlChecklistRepository.findVehicleControlChecklistByJobCard(this).isPresent) {
            controlChecklistRepository.findVehicleControlChecklistByJobCard(this).get().id
        } else null
        val stateChecklistId = if (vehicleStateRepository.findVehicleStateChecklistByJobCard(this).isPresent) {
            vehicleStateRepository.findVehicleStateChecklistByJobCard(this).get().id
        } else null

        return ResponseJobCard(
            id = this.jobId!!,
            jobCardName = this.jobCardName!!,
            jobCardNumber = this.jobCardNumber!!,
            vehicleId = vehicle.id!!,
            vehicleName = vehicle.model!!,
            clientId = client.id!!,
            clientName = client.clientName + " " + client.clientSurname,
            serviceAdvisorId = serviceAdvisor.employeeId!!,
            serviceAdvisorName = serviceAdvisor.employeeName + " " + serviceAdvisor.employeeSurname,
            supervisorId = supervisor.employeeId!!,
            supervisorName = supervisor.employeeName + " " + supervisor.employeeSurname,
            timesheets = timesheets,
            controlChecklistId = controlChecklistId,
            stateChecklistId = stateChecklistId,
            serviceChecklistId = serviceChecklistId,
            priority = this.priority,
            dateAndTimeIn = this.dateAndTimeIn,
            dateAndTimeFrozen = this.dateAndTimeFrozen,
            dateAndTimeClosed = this.dateAndTimeClosed,
            estimatedTimeOfCompletion = this.estimatedTimeOfCompletion,
            jobCardDeadline = this.jobCardDeadline
        )
    }
}