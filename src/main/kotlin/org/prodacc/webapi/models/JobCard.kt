package org.prodacc.webapi.models

import jakarta.persistence.*
import java.util.UUID
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "jobcards")
data class JobCard(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "\"job_id\"", nullable = false)
    var job_id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"vehicle_reference_id\"", nullable = false)
    var vehicleReference: Vehicle? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"customer_reference_id\"", nullable = false)
    var customerReference: Client? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"service_advisor\"", nullable = false)
    var serviceAdvisor: Employee? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor", nullable = false)
    var supervisor: Employee? = null,

    @ColumnDefault("2024-04-21 09:21:35.510973")
    @Column(name = "\"date_and_time_in\"", nullable = false)
    var dateAndTimeIn: Instant? = null,

    @Column(name = "\"estimated_time_of_completion\"")
    var estimatedTimeOfCompletion: Instant? = null,

    @Column(name = "\"date_and_time_frozen\"")
    var dateAndTimeFrozen: Instant? = null,

    @Column(name = "\"date_and_time_closed\"")
    var dateAndTimeClosed: Instant? = null,

    @Column(name = "\"service_advisor_report\"", nullable = false, length = 1000)
    var serviceAdvisorReport: String? = null,

    @Column(name = "\"technician_diagnostic_report\"", length = 1000)
    var technicianDiagnosticReport: String? = null,

    @Column(name = "\"supervisor_report\"", length = 1000)
    var supervisorReport: String? = null,

    @Column(name = "\"job_card_status\"", nullable = false, length = 1000)
    var jobCardStatus: String? = null,

    @Column(name = "\"job_card_name\"", nullable = false, length = 1000)
    var jobCardName: String? = null,

    @Column(name = "\"job_card_number\"", nullable = false)
    var jobCardNumber: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "technician")
    var technician: Employee? = null,

    @ColumnDefault("false")
    @Column(name = "priority")
    var priority: Boolean? = null,

    @Column(name = "\"job_card_deadline\"")
    var jobCardDeadline: Instant? = null,

    @Column(name = "\"work_done\"", length = 5000)
    var workDone: String? = null,

    @Column(name = "\"additional_work_done\"", length = 5000)
    var additionalWorkDone: String? = null,

    @OneToOne(mappedBy = "jobCard")
    var servicechecklists: VehicleServiceChecklist? = null,

    @OneToMany(mappedBy = "jobCardUUID")
    var timesheets: MutableSet<Timesheet> = mutableSetOf(),

    @OneToOne(mappedBy = "jobCard")
    var vehiclechecklists: VehicleStateChecklist? = null,

    @OneToOne(mappedBy = "jobCard")
    var vehiclecontrolchecklists: VehicleControlChecklist? = null
) {
}
