package org.prodacc.webapi.models.products

import jakarta.persistence.*
import org.prodacc.webapi.models.ProductVehicle
import java.util.*

@Deprecated(message = "this version of Product Vehicle is deprecated", replaceWith = ReplaceWith("org.prodacc.webapi.models.ProductVehicle"))
@Entity
@Table(name = "product_vehicle")
data class ProductVehicle (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"id\"", nullable = false)
    val id: UUID,

    @Column(name = "\"make\"", nullable = false)
    val make: String,

    @Column(name = "\"model\"", nullable = false)
    val model: String,

    @Column(name = "\"year\"", nullable = false)
    val year: Int,

    @OneToMany(mappedBy = "vehicleId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val productReference: MutableSet<ProductVehicleReference> = mutableSetOf()
) {
    internal constructor(): this(
        id = UUID.randomUUID(),
        make = "",
        model = "",
        year = 0,
        productReference = mutableSetOf()
    )
}