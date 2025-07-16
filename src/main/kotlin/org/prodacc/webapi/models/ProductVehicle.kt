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
import org.prodacc.webapi.models.products.Product
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "product_vehicles")
data class ProductVehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "vehicle_make", nullable = false)
    val vehicleMake: String,

    @Column(name = "vehicle_model")
    val vehicleModel: String? = null,

    @Column(name = "year_from")
    val yearFrom: Int? = null,

    @Column(name = "year_to")
    val yearTo: Int? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)