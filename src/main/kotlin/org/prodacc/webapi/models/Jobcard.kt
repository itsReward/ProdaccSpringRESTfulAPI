package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.util.*

@Entity
@Table(name = "jobcards")
open class Jobcard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "\"JobID\"", nullable = false)
    open var id: UUID? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"vehicleReferenceId\"", nullable = false)
    open var vehicleReference: org.prodacc.webapi.models.Vehicle? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"customerReferenceId\"", nullable = false)
    open var customerReference: Client? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"serviceAdvisor\"", nullable = false)
    open var serviceAdvisor: Employee? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor", nullable = false)
    open var supervisor: Employee? = null

    @ColumnDefault("2024-04-21 09:21:35.510973")
    @Column(name = "\"dateAndTimeIn\"", nullable = false)
    open var dateAndTimeIn: Instant? = null

    @Column(name = "\"estimatedTimeOfCompletion\"")
    open var estimatedTimeOfCompletion: Instant? = null

    @Column(name = "\"dateAndTimeFrozen\"")
    open var dateAndTimeFrozen: Instant? = null

    @Column(name = "\"dateAndTimeClosed\"")
    open var dateAndTimeClosed: Instant? = null

    @Column(name = "\"serviceAdvisorReport\"", nullable = false, length = 1000)
    open var serviceAdvisorReport: String? = null

    @Column(name = "\"technicianDiagnosticReport\"", length = 1000)
    open var technicianDiagnosticReport: String? = null

    @Column(name = "\"supervisorReport\"", length = 1000)
    open var supervisorReport: String? = null

    @Column(name = "\"jobCardStatus\"", nullable = false, length = 1000)
    open var jobCardStatus: String? = null

    @Column(name = "\"jobCardName\"", nullable = false, length = 1000)
    open var jobCardName: String? = null

    @Column(name = "\"jobCardNumber\"", nullable = false)
    open var jobCardNumber: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "technician")
    open var technician: Employee? = null

    @ColumnDefault("false")
    @Column(name = "priority")
    open var priority: Boolean? = null

    @Column(name = "\"jobCardDeadline\"")
    open var jobCardDeadline: Instant? = null

    @Column(name = "\"workDone\"", length = 5000)
    open var workDone: String? = null

    @Column(name = "\"additionalWorkDone\"", length = 5000)
    open var additionalWorkDone: String? = null

    @OneToOne(mappedBy = "jobCard")
    open var servicechecklists: org.prodacc.webapi.models.VehicleServiceChecklist? = null

    @OneToMany(mappedBy = "jobCardUUID")
    open var timesheets: MutableSet<org.prodacc.webapi.models.Timesheet> = mutableSetOf()

    @OneToOne(mappedBy = "jobCard")
    open var vehiclechecklists: org.prodacc.webapi.models.VehicleStateChecklist? = null

    @OneToOne(mappedBy = "jobCard")
    open var vehiclecontrolchecklists: org.prodacc.webapi.models.VehicleControlChecklist? = null
}