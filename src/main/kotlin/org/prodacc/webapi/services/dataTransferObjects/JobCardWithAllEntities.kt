package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.*
import java.util.*

data class JobCardWithAllEntities(
    var jobCard: JobCard? = null,
    var client: Client? = null,
    var vehicle: Vehicle? = null,
    var supervisor: Employee? = null,
    var serviceAdvisor: Employee? = null,
    var technician: Employee? = null,
    val timesheet: List<Timesheet> = listOf(),
    val vehicleServiceChecklist: VehicleServiceChecklist? = null,
    val vehicleControlChecklist: VehicleControlChecklist? = null,
    val vehicleStateChecklist: VehicleStateChecklist? = null
)
