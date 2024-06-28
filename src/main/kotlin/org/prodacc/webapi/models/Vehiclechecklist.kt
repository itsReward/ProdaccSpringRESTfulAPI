package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "vehiclechecklists")
open class Vehiclechecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"jobCardId\"", nullable = false)
    open var jobCard: Jobcard? = null

    @Column(name = "\"millage in\"", nullable = false, length = 50)
    open var millageIn: String? = null

    @Column(name = "\"millage out\"", nullable = false, length = 50)
    open var millageOut: String? = null

    @Column(name = "\"fuel level in\"", nullable = false, length = 50)
    open var fuelLevelIn: String? = null

    @Column(name = "\"fuel level out\"", nullable = false, length = 50)
    open var fuelLevelOut: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checklist", nullable = false)
    open var checklist: MutableMap<String, Any>? = null
}