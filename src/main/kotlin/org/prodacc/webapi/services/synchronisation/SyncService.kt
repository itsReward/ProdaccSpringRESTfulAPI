package org.prodacc.webapi.services.synchronisation

import org.prodacc.webapi.models.*
import org.prodacc.webapi.repositories.*
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Service
class SyncService(
    private val webSocketHandler: WebSocketHandler,
    private val jobCardRepository: JobCardRepository,
    private val timesheetRepository: TimesheetRepository,
    private val employeeRepository: EmployeeRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository,
    private val serviceChecklistRepository: ServiceChecklistRepository,
    private val controlChecklistRepository: ControlChecklistRepository,
    private val vehicleStateChecklistRepository: VehicleStateChecklistRepository,
    private val userRepository: UserRepository,
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val jobCardTechniciansRepository: JobCardTechniciansRepository,
    private val jobCardReportsRepository: JobCardReportsRepository
) {

    fun syncToOffline(entity: Any) {
        val updateType = when (entity) {
            is JobCard -> "jobCard"
            is Timesheet -> "timesheet"
            is Vehicle -> "vehicle"
            is Client -> "client"
            is VehicleServiceChecklist -> "vehicleServiceChecklist"
            is VehicleControlChecklist -> "vehicleControlChecklist"
            is VehicleStateChecklist -> "vehicleStateChecklist"
            is User -> "user"
            is Employee -> "employee"
            is JobCardStatus -> "jobCardStatus"
            is JobCardTechnicians -> "jobCardTechnicians"
            is JobCardReports -> "jobCardReports"
            else -> throw IllegalArgumentException("Unknown entity type")
        }

        webSocketHandler.broadcastUpdate(updateType, entity)
    }

    @PostMapping("/sync") // Change to REST endpoint
    fun syncFromOffline(@RequestBody syncData: SyncData) {
        val savedEntity = when (syncData.entityType) {
            "JobCard" -> jobCardRepository.save(syncData.entity as JobCard)
            "Timesheet" -> timesheetRepository.save(syncData.entity as Timesheet)
            "Vehicle" -> vehicleRepository.save(syncData.entity as Vehicle)
            "Client" -> clientRepository.save(syncData.entity as Client)
            "VehicleServiceChecklist" -> vehicleStateChecklistRepository.save(syncData.entity as VehicleStateChecklist)
            "VehicleControlChecklist" -> vehicleStateChecklistRepository.save(syncData.entity as VehicleStateChecklist)
            "VehicleStateChecklist" -> vehicleStateChecklistRepository.save(syncData.entity as VehicleStateChecklist)
            "User" -> userRepository.save(syncData.entity as User)
            "Employee" -> employeeRepository.save(syncData.entity as Employee)
            "JobCardStatus" -> jobCardStatusRepository.save(syncData.entity as JobCardStatus)
            "JobCardTechnicians" -> jobCardTechniciansRepository
            "JobCardReports" -> jobCardReportsRepository.save(syncData.entity as JobCardReports)
            else -> throw IllegalArgumentException("Unknown entity type")

        }

        // Broadcast the update to all connected clients
        webSocketHandler.broadcastUpdate(syncData.entityType.lowercase(), savedEntity)
    }
}