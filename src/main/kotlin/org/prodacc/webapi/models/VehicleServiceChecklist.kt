package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "vehicle_service_checklists")
data class VehicleServiceChecklist (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @DefaultValue("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"job_card_id\"", nullable = false)
    var jobCard: JobCard? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "technician", nullable = false)
    var technician: Employee? = null,

    @ColumnDefault("2024-05-01 16:23:48.215561")
    @Column(name = "created", nullable = false)
    var created: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checklist", nullable = false)
    var checklist: MutableMap<String, Any>? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,
)