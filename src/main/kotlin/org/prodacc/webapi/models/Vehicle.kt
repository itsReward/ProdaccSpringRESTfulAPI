package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(name = "vehicles")
open class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "\"vehicleId\"", nullable = false)
    open var id: UUID? = null

    @Column(name = "model", nullable = false, length = 100)
    open var model: String? = null

    @Column(name = "\"regNumber\"", length = 100)
    open var regNumber: String? = null

    @Column(name = "make", nullable = false, length = 100)
    open var make: String? = null

    @Column(name = "color", nullable = false, length = 100)
    open var color: String? = null

    @Column(name = "\"chassisNumber\"", length = 100)
    open var chassisNumber: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"clientReferenceId\"", nullable = false)
    open var clientReference: Client? = null

    @OneToOne(mappedBy = "vehicleReference")
    open var jobcards: Jobcard? = null
}