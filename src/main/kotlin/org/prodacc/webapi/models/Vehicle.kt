package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(name = "vehicles")
data class Vehicle (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "\"vehicle_id\"", nullable = false)
    var id: UUID? = null,

    @Column(name = "model", nullable = false, length = 100)
    var model: String? = null,

    @Column(name = "\"reg_number\"", length = 100)
    var regNumber: String? = null,

    @Column(name = "make", nullable = false, length = 100)
    var make: String? = null,

    @Column(name = "color", nullable = false, length = 100)
    var color: String? = null,

    @Column(name = "\"chassis_number\"", length = 100)
    var chassisNumber: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"client_reference_id\"", nullable = false)
    var clientReference: Client? = null,

    @OneToOne(mappedBy = "vehicleReference")
    var jobcards: JobCard? = null
)