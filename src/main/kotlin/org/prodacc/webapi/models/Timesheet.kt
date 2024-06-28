package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.util.*

@Entity
@Table(name = "timesheets")
open class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"timesheetId\"", nullable = false)
    open var id: UUID? = null

    @Column(name = "\"clockInDateAndTime\"", nullable = false)
    open var clockInDateAndTime: Instant? = null

    @Column(name = "\"sheetTitle\"", nullable = false, length = 500)
    open var sheetTitle: String? = null

    @Column(name = "report", length = 2000)
    open var report: String? = null

    @Column(name = "\"clockOutDateAndTime\"")
    open var clockOutDateAndTime: Instant? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"jobCardUUID\"", nullable = false)
    open var jobCardUUID: Jobcard? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"employeeId\"", nullable = false)
    open var employee: Employee? = null
}