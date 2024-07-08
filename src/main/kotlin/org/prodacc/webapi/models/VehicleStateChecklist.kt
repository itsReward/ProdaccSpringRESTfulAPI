package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "vehicle_state_checklists")
data class VehicleStateChecklist (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"job_card_id\"", nullable = false)
    var jobCard: JobCard? = null,

    @Column(name = "\"millage_in\"", nullable = false, length = 50)
    var millageIn: String? = null,

    @Column(name = "\"millage_out\"", nullable = false, length = 50)
    var millageOut: String? = null,

    @Column(name = "\"fuel_level_in\"", nullable = false, length = 50)
    var fuelLevelIn: String? = null,

    @Column(name = "\"fuel_level_out\"", nullable = false, length = 50)
    var fuelLevelOut: String? = null,

    @Column(name = "\"created\"", nullable = false)
    var created: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checklist", nullable = false)
    var checklist: MutableMap<String, Any>? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,

)