package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.Appointment
import org.prodacc.webapi.models.AppointmentStatus
import org.prodacc.webapi.repositories.AppointmentRepository
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.AppointmentResponseDto
import org.prodacc.webapi.services.dataTransferObjects.CreateAppointmentDto
import org.prodacc.webapi.services.dataTransferObjects.RescheduleAppointmentDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val clientRepository: ClientRepository,
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository
) {
    private val logger = LoggerFactory.getLogger(AppointmentService::class.java)

    fun getAllAppointments(): List<AppointmentResponseDto> {
        logger.info("Fetching all appointments")
        return appointmentRepository.findAll().map { it.toDto() }
    }

    fun getUpcomingAppointments(): List<AppointmentResponseDto> {
        logger.info("Fetching upcoming appointments")
        return appointmentRepository.findUpcomingAppointments().map { it.toDto() }
    }

    fun getAppointmentById(id: UUID): AppointmentResponseDto {
        logger.info("Fetching appointment with ID: $id")
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Appointment not found with ID: $id") }
        return appointment.toDto()
    }

    fun createAppointment(createDto: CreateAppointmentDto): AppointmentResponseDto {
        logger.info("Creating new appointment for client: ${createDto.clientId}")

        val client = clientRepository.findById(createDto.clientId)
            .orElseThrow { EntityNotFoundException("Client not found with ID: ${createDto.clientId}") }

        val vehicle = vehicleRepository.findById(createDto.vehicleId)
            .orElseThrow { EntityNotFoundException("Vehicle not found with ID: ${createDto.vehicleId}") }

        val assignedTechnician = createDto.assignedTechnicianId?.let {
            employeeRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Employee not found with ID: $it") }
        }

        // Check for scheduling conflicts
        val endTime = createDto.appointmentTime.plusMinutes(createDto.durationMinutes.toLong())
        val conflicts = appointmentRepository.findConflictingAppointments(
            createDto.appointmentDate,
            createDto.appointmentTime,
            endTime
        )

        if (conflicts.isNotEmpty()) {
            throw IllegalArgumentException("Appointment slot is already booked")
        }

        val appointment = Appointment(
            client = client,
            vehicle = vehicle,
            appointmentDate = createDto.appointmentDate,
            appointmentTime = createDto.appointmentTime,
            durationMinutes = createDto.durationMinutes,
            serviceType = createDto.serviceType,
            description = createDto.description,
            priority = createDto.priority,
            assignedTechnician = assignedTechnician,
            notes = createDto.notes
        )

        val savedAppointment = appointmentRepository.save(appointment)
        logger.info("Successfully created appointment with ID: ${savedAppointment.appointmentId}")

        return savedAppointment.toDto()
    }

    fun rescheduleAppointment(id: UUID, rescheduleDto: RescheduleAppointmentDto): AppointmentResponseDto {
        logger.info("Rescheduling appointment with ID: $id")

        val appointment = appointmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Appointment not found with ID: $id") }

        // Check for scheduling conflicts
        val endTime = rescheduleDto.appointmentTime.plusMinutes(appointment.durationMinutes.toLong())
        val conflicts = appointmentRepository.findConflictingAppointments(
            rescheduleDto.appointmentDate,
            rescheduleDto.appointmentTime,
            endTime
        ).filter { it.appointmentId != id } // Exclude current appointment

        if (conflicts.isNotEmpty()) {
            throw IllegalArgumentException("New appointment slot is already booked")
        }

        val updatedAppointment = appointment.copy(
            appointmentDate = rescheduleDto.appointmentDate,
            appointmentTime = rescheduleDto.appointmentTime,
            notes = rescheduleDto.notes ?: appointment.notes,
            reminderSent = false, // Reset reminder status
            updatedAt = LocalDateTime.now()
        )

        val savedAppointment = appointmentRepository.save(updatedAppointment)
        logger.info("Successfully rescheduled appointment with ID: $id")

        return savedAppointment.toDto()
    }

    fun updateAppointmentStatus(id: UUID, status: AppointmentStatus): AppointmentResponseDto {
        logger.info("Updating appointment status to $status for ID: $id")

        val appointment = appointmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Appointment not found with ID: $id") }

        val updatedAppointment = appointment.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )

        val savedAppointment = appointmentRepository.save(updatedAppointment)
        logger.info("Successfully updated appointment status for ID: $id")

        return savedAppointment.toDto()
    }

    fun cancelAppointment(id: UUID): String {
        logger.info("Cancelling appointment with ID: $id")

        val appointment = appointmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Appointment not found with ID: $id") }

        val cancelledAppointment = appointment.copy(
            status = AppointmentStatus.CANCELLED,
            updatedAt = LocalDateTime.now()
        )

        appointmentRepository.save(cancelledAppointment)
        logger.info("Successfully cancelled appointment with ID: $id")

        return "Appointment successfully cancelled"
    }

    fun getClientAppointments(clientId: UUID): List<AppointmentResponseDto> {
        logger.info("Fetching appointments for client: $clientId")
        return appointmentRepository.findByClientOrderByDateDesc(clientId).map { it.toDto() }
    }
}