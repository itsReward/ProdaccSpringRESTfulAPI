package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.util.*

@Entity
@Table(name = "timesheets")
data class Timesheet (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"timesheet_id\"", nullable = false)
    var id: UUID? = null,

    @Column(name = "\"clock_in_date_and_time\"", nullable = false)
    var clockInDateAndTime: Instant? = null,

    @Column(name = "\"sheet_title\"", nullable = false, length = 500)
    var sheetTitle: String? = null,

    @Column(name = "report", length = 2000)
    var report: String? = null,

    @Column(name = "\"clock_out_date_and_time\"")
    var clockOutDateAndTime: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"job_card_id\"", nullable = false)
    var jobCardUUID: JobCard? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"employee_id\"", nullable = false)
    var employee: Employee? = null
)