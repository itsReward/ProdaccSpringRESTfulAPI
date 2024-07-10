package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.JobCard
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class ResponseServiceChecklistWithJobCard(
    val id: UUID? = null,
    val jobCardId: UUID? = null,
    val jobCardName: String? = null,
    val technicianId:UUID? = null,
    val technicianName:String? = null,
    val created: LocalDateTime? = null,
    val checklist: MutableMap<String, Any>? = null
)
