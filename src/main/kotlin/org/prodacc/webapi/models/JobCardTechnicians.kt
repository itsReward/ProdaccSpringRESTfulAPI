package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "job_card_technicians")
@IdClass(JobCardTechnicianId::class)
data class JobCardTechnicians(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "job_card_id", nullable = false)
    var jobCardId: JobCard? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "employee_id", nullable = false)
    var employeeId: Employee? = null
)

data class JobCardTechnicianId(
    var jobCardId: UUID? = null,
    var employeeId: UUID? = null
) : Serializable
