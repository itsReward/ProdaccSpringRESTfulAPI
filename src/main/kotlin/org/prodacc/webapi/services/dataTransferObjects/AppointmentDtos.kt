package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Appointment
import org.prodacc.webapi.models.AppointmentPriority
import org.prodacc.webapi.models.AppointmentStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class CreateAppointmentDto(
    val clientId: UUID,
    val vehicleId: UUID,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val durationMinutes: Int = 60,
    val serviceType: String? = null,
    val description: String? = null,
    val priority: AppointmentPriority = AppointmentPriority.NORMAL,
    val assignedTechnicianId: UUID? = null,
    val notes: String? = null
)

data class AppointmentResponseDto(
    val appointmentId: UUID,
    val clientName: String,
    val clientSurname: String,
    val clientPhone: String?,
    val vehicleInfo: String,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val durationMinutes: Int,
    val serviceType: String?,
    val description: String?,
    val status: AppointmentStatus,
    val priority: AppointmentPriority,
    val assignedTechnicianName: String?,
    val reminderSent: Boolean,
    val notes: String?,
    val createdAt: LocalDateTime
)

data class RescheduleAppointmentDto(
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val notes: String? = null
)

fun Appointment.toDto(): AppointmentResponseDto = AppointmentResponseDto(
    appointmentId = this.appointmentId!!,
    clientName = this.client?.clientName ?: "",
    clientSurname = this.client?.clientSurname ?: "",
    clientPhone = this.client?.phone,
    vehicleInfo = "${this.vehicle?.make} ${this.vehicle?.model} (${this.vehicle?.regNumber})",
    appointmentDate = this.appointmentDate!!,
    appointmentTime = this.appointmentTime!!,
    durationMinutes = this.durationMinutes,
    serviceType = this.serviceType,
    description = this.description,
    status = this.status,
    priority = this.priority,
    assignedTechnicianName = this.assignedTechnician?.let { "${it.employeeName} ${it.employeeSurname}" },
    reminderSent = this.reminderSent,
    notes = this.notes,
    createdAt = this.createdAt
)