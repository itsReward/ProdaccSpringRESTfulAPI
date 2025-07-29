package org.prodacc.webapi.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "product_vehicle_reference")
@IdClass(ProductVehicleReferenceId::class)
data class ProductVehicleReference(
    @Id
    @Column(name = "product_id", nullable = false)
    val productId: UUID = UUID.randomUUID(),

    @Id
    @Column(name = "vehicle_id", nullable = false)
    val vehicleId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    val product: Product? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", insertable = false, updatable = false)
    val vehicle: ProductVehicle? = null
)

// Composite key class
data class ProductVehicleReferenceId(
    val productId: UUID = UUID.randomUUID(),
    val vehicleId: UUID = UUID.randomUUID()
) : java.io.Serializable