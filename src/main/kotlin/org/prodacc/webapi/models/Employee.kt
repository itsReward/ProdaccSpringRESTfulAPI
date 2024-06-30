package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.util.*

@Entity
@Table(name = "employees")
open class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"employeeID\"", nullable = false)
    open var employee_id: UUID? = null

    @ColumnDefault("unknown")
    @Column(name = "\"employeeName\"", nullable = false, length = 50)
    open var employeeName: String? = null

    @ColumnDefault("unknown")
    @Column(name = "\"employeeSurname\"", nullable = false, length = 50)
    open var employeeSurname: String? = null

    @ColumnDefault("0.0")
    @Column(name = "rating", nullable = false)
    open var rating: Float? = null

    @ColumnDefault("unassigned")
    @Column(name = "\"employeeRole\"", nullable = false, length = 50)
    open var employeeRole: String? = null

    @ColumnDefault("department")
    @Column(name = "\"employeeDepartment\"", nullable = false, length = 80)
    open var employeeDepartment: String? = null

    @ColumnDefault("")
    @Column(name = "\"phoneNumber\"", length = 500)
    open var phoneNumber: String? = null

    @ColumnDefault("home address")
    @Column(name = "\"Home Address\"", nullable = false, length = 500)
    open var homeAddress: String? = null

    @OneToOne(mappedBy = "serviceAdvisor")
    open var jobcardsServiceAdvisor: org.prodacc.webapi.models.Jobcard? = null

    @OneToOne(mappedBy = "supervisor")
    open var jobcardsSupervisor: org.prodacc.webapi.models.Jobcard? = null

    @OneToOne(mappedBy = "technician")
    open var jobcardsTechnician: org.prodacc.webapi.models.Jobcard? = null

    @OneToOne(mappedBy = "technician")
    open var servicechecklists: org.prodacc.webapi.models.VehicleServiceChecklist? = null

    @OneToOne(mappedBy = "employee")
    open var timesheets: org.prodacc.webapi.models.Timesheet? = null

    @OneToOne(mappedBy = "technician")
    open var vehiclecontrolchecklists: org.prodacc.webapi.models.VehicleControlChecklist? = null
}