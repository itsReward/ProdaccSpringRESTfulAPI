package org.prodacc.webapi.models

import jakarta.persistence.*

@Entity
@Table(name = "job_card_reports")
data class JobCardReports(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_card_report_id")
    var jobCardReportId: Int? = null,

    @Column(name = "type_of_report", nullable = false)
    var typeOfReport: String? = null,

    @Column(name = "job_report", nullable = false)
    var jobReport: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    var employee: Employee? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    var jobCardId: JobCard? = null,
)
