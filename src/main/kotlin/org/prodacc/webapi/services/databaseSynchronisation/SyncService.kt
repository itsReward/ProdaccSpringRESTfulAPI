package org.prodacc.webapi.services.databaseSynchronisation

import org.prodacc.webapi.models.*
import org.prodacc.webapi.repositories.*
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class SyncService(
    private val jobCardRepository: JobCardRepository,
    private val timesheetRepository: TimesheetRepository,
    private val employeeRepository: EmployeeRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository,
    private val serviceChecklistRepository: ServiceChecklistRepository,
    private val controlChecklistRepository: ControlChecklistRepository,
    private val vehicleStateChecklistRepository: VehicleStateChecklistRepository,
    private val userRepository: UserRepository,
    private val template: SimpMessagingTemplate,
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardTechniciansRepository: JobCardTechniciansRepository,
    private val jobCardReportsRepository: JobCardReportsRepository
) {
    fun syncToOffline(entity: Any) {
        when (entity) {
            is JobCard -> template.convertAndSend("/topic/sync/jobCard", entity)
            is Timesheet -> template.convertAndSend("/topic/sync/timesheet", entity)
            is Vehicle -> template.convertAndSend("/topic/sync/vehicle", entity)
            is Client -> template.convertAndSend("/topic/sync/client", entity)
            is VehicleServiceChecklist -> template.convertAndSend("/topic/sync/vehicle_service_checklist", entity)
            is VehicleControlChecklist -> template.convertAndSend("/topic/sync/vehicle_control_checklist", entity)
            is VehicleStateChecklist -> template.convertAndSend("/topic/sync/vehicle_state_checklist", entity)
            is User -> template.convertAndSend("/topic/sync/user", entity)
            is Employee -> template.convertAndSend("/topic/sync/employee", entity)
            is JobCardStatus -> template.convertAndSend("/topic/sync/jobCardStatus", entity)
            is JobCardTechnicians -> template.convertAndSend("/topic/sync/jobCardTechnicians", entity)
            is JobCardReports -> template.convertAndSend("/topic/sync/jobCardReports", entity)
        }
    }

    @MessageMapping("/sync/offline")
    fun syncFromOffline(syncData: SyncData) {
        when (syncData.entityType) {
            "JobCard" -> jobCardRepository.save(syncData.entity as JobCard)
            "Timesheet" -> timesheetRepository.save(syncData.entity as Timesheet)
            "Employee" -> employeeRepository.save(syncData.entity as Employee)
            "Vehicle" -> vehicleRepository.save(syncData.entity as Vehicle)
            "Client" -> clientRepository.save(syncData.entity as Client)
            "VehicleServiceChecklist" -> serviceChecklistRepository.save(syncData.entity as VehicleServiceChecklist)
            "VehicleControlChecklist" -> controlChecklistRepository.save(syncData.entity as VehicleControlChecklist)
            "VehicleStateChecklist" -> vehicleStateChecklistRepository.save(syncData.entity as VehicleStateChecklist)
            "User" -> userRepository.save(syncData.entity as User)
            "JobCardStatus" -> jobCardStatusRepository.save(syncData.entity as JobCardStatus)
            "JobCardTechnicians" -> jobCardTechniciansRepository.save(syncData.entity as JobCardTechnicians)
            "JobCardReports" -> jobCardReportsRepository.save(syncData.entity as JobCardReports)

        }
        // Broadcast the update to all other clients
        template.convertAndSend("/topic/sync/${syncData.entityType.lowercase()}", syncData.entity)
    }
}