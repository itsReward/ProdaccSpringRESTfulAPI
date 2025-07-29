package org.prodacc.webapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "product_vehicle")
data class ProductVehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "make", nullable = false)
    val vehicleMake: String = "undefined",

    @Column(name = "model")
    val vehicleModel: String? = null,

    @Column(name = "year")
    val year: Int = 1,

)