package org.prodacc.webapi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "appointments")
data class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "appointment_id", nullable = false)
    val appointmentId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    val client: Client,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    val vehicle: Vehicle,

    @Column(name = "appointment_date", nullable = false)
    val appointmentDate: java.time.LocalDate,

    @Column(name = "appointment_time", nullable = false)
    val appointmentTime: java.time.LocalTime,

    @Column(name = "duration_minutes")
    val durationMinutes: Int = 60,

    @Column(name = "service_type")
    val serviceType: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: AppointmentStatus = AppointmentStatus.SCHEDULED,

    @Column(name = "reminder_sent")
    var reminderSent: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    val priority: AppointmentPriority = AppointmentPriority.NORMAL,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician")
    val assignedTechnician: Employee? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class AppointmentStatus {
    SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
}

enum class AppointmentPriority {
    LOW, NORMAL, HIGH, URGENT
}
