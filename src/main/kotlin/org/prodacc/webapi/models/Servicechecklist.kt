package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "servicechecklists")
open class Servicechecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"jobCardId\"", nullable = false)
    open var jobCard: Jobcard? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "technician", nullable = false)
    open var technician: Employee? = null

    @ColumnDefault("2024-05-01 16:23:48.215561")
    @Column(name = "created", nullable = false)
    open var created: Instant? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checklist", nullable = false)
    open var checklist: MutableMap<String, Any>? = null
}