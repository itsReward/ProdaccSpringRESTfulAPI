package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.AppointmentStatus
import org.prodacc.webapi.services.AppointmentService
import org.prodacc.webapi.services.dataTransferObjects.AppointmentResponseDto
import org.prodacc.webapi.services.dataTransferObjects.CreateAppointmentDto
import org.prodacc.webapi.services.dataTransferObjects.RescheduleAppointmentDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = ["*"])
class AppointmentController(
    private val appointmentService: AppointmentService
) {

    @GetMapping("/all")
    fun getAllAppointments(): ResponseEntity<List<AppointmentResponseDto>> =
        ResponseEntity.ok(appointmentService.getAllAppointments())

    @GetMapping("/upcoming")
    fun getUpcomingAppointments(): ResponseEntity<List<AppointmentResponseDto>> =
        ResponseEntity.ok(appointmentService.getUpcomingAppointments())

    @GetMapping("/{id}")
    fun getAppointment(@PathVariable id: UUID): ResponseEntity<AppointmentResponseDto> =
        try {
            ResponseEntity.ok(appointmentService.getAppointmentById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createAppointment(@RequestBody createDto: CreateAppointmentDto): ResponseEntity<AppointmentResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(createDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/reschedule/{id}")
    fun rescheduleAppointment(
        @PathVariable id: UUID,
        @RequestBody rescheduleDto: RescheduleAppointmentDto
    ): ResponseEntity<AppointmentResponseDto> =
        try {
            ResponseEntity.ok(appointmentService.rescheduleAppointment(id, rescheduleDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/{id}/status")
    fun updateAppointmentStatus(
        @PathVariable id: UUID,
        @RequestParam status: AppointmentStatus
    ): ResponseEntity<AppointmentResponseDto> =
        try {
            ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/cancel/{id}")
    fun cancelAppointment(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(appointmentService.cancelAppointment(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/client/{clientId}")
    fun getClientAppointments(@PathVariable clientId: UUID): ResponseEntity<List<AppointmentResponseDto>> =
        ResponseEntity.ok(appointmentService.getClientAppointments(clientId))
}