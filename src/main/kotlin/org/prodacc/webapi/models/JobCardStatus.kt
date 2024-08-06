package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "job_card_status")
data class JobCardStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "job_card_status_id")
    var jobCardStatusId : UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"job_card_id\"", nullable = false)
    var jobCardId: JobCard? = null,

    @Column(name = "status")
    var status: String? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
