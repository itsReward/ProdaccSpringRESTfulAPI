package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Appointment
import org.prodacc.webapi.models.AppointmentStatus
import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.Employee
import org.prodacc.webapi.models.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface AppointmentRepository : JpaRepository<Appointment, UUID> {
    fun findByClient(client: Client): List<Appointment>
    fun findByVehicle(vehicle: Vehicle): List<Appointment>
    fun findByStatus(status: AppointmentStatus): List<Appointment>
    fun findByAppointmentDate(appointmentDate: LocalDate): List<Appointment>
    fun findByAppointmentDateBetween(startDate: LocalDate, endDate: LocalDate): List<Appointment>
    fun findByAssignedTechnician(technician: Employee): List<Appointment>

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.reminderSent = false " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    fun findByAppointmentDateAndReminderSentFalse(@Param("date") date: LocalDate): List<Appointment>

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= CURRENT_DATE " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    fun findUpcomingAppointments(): List<Appointment>

    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    fun findByClientOrderByDateDesc(@Param("clientId") clientId: UUID): List<Appointment>

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date " +
            "AND a.appointmentTime BETWEEN :startTime AND :endTime " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED')")
    fun findConflictingAppointments(@Param("date") date: LocalDate,
                                    @Param("startTime") startTime: java.time.LocalTime,
                                    @Param("endTime") endTime: java.time.LocalTime): List<Appointment>

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = CURRENT_DATE " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS')")
    fun countTodaysAppointments(): Long
}
