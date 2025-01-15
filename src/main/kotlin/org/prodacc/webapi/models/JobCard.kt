package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "jobcards")
data class JobCard(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "\"job_id\"", nullable = false)
    var jobId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @Column(name = "\"date_and_time_in\"", nullable = false)
    var dateAndTimeIn: LocalDateTime? = null,

    @Column(name = "\"estimated_time_of_completion\"")
    var estimatedTimeOfCompletion: LocalDateTime? = null,

    @Column(name = "\"date_and_time_frozen\"")
    var dateAndTimeFrozen: LocalDateTime? = null,

    @Column(name = "\"date_and_time_closed\"")
    var dateAndTimeClosed: LocalDateTime? = null,



    @Column(name = "\"job_card_name\"", nullable = false, length = 1000)
    var jobCardName: String? = null,

    @Column(name = "\"job_card_number\"", nullable = false)
    var jobCardNumber: Int? = null,


    @ColumnDefault("false")
    @Column(name = "priority")
    var priority: Boolean? = null,

    @Column(name = "\"job_card_deadline\"")
    var jobCardDeadline: LocalDateTime? = null,

    @OneToMany(mappedBy = "jobCardId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var technicians: List<JobCardTechnicians> = emptyList(),

    @OneToOne(mappedBy = "jobCard", cascade = [CascadeType.ALL], orphanRemoval = true)
    var servicechecklists: VehicleServiceChecklist? = null,

    @OneToMany(mappedBy = "jobCardUUID", cascade = [CascadeType.ALL], orphanRemoval = true)
    var timesheets: MutableSet<Timesheet> = mutableSetOf(),

    @OneToOne(mappedBy = "jobCard", cascade = [CascadeType.ALL], orphanRemoval = true)
    var vehiclechecklists: VehicleStateChecklist? = null,

    @OneToOne(mappedBy = "jobCard", cascade = [CascadeType.ALL], orphanRemoval = true)
    var vehiclecontrolchecklists: VehicleControlChecklist? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,
) {
}
