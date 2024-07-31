package org.prodacc.webapi.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "employees")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"employee_id\"", nullable = false)
    var employeeId: UUID? = null,

    @Column(name = "\"employee_name\"", nullable = false, length = 50)
    var employeeName: String = "unassigned",

    @Column(name = "\"employee_surname\"", nullable = false, length = 50)
    var employeeSurname: String = "unassigned",

    @Column(name = "rating", nullable = false)
    var rating: Float = 0.0f,

    @Column(name = "\"employee_role\"", nullable = false, length = 50)
    var employeeRole: String = "unassigned",

    @Column(name = "\"employee_department\"", nullable = false, length = 80)
    var employeeDepartment: String = "unassigned",

    @Column(name = "\"phone_number\"", length = 500)
    var phoneNumber: String = "not_entered",

    @Column(name = "\"home_address\"", nullable = false, length = 500)
    var homeAddress: String = "unassigned",

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,

    @OneToMany(mappedBy = "employeeId", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    var jobCards: List<JobCardTechnicians> = emptyList(),

    @OneToMany(mappedBy = "serviceAdvisor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var jobcardsServiceAdvisor: List<JobCard> = mutableListOf(),

    @OneToMany(mappedBy = "supervisor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var jobcardsSupervisor: List<JobCard> = mutableListOf(),

    @OneToMany(mappedBy = "technician", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var servicechecklists: List<VehicleServiceChecklist> = mutableListOf(),

    @OneToMany(mappedBy = "employee", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var timesheets: List<Timesheet> = mutableListOf(),

    @OneToMany(mappedBy = "technician", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var vehiclecontrolchecklists: List<VehicleControlChecklist> = mutableListOf()
)
