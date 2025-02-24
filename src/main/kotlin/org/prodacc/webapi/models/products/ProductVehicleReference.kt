package org.prodacc.webapi.models.products

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.prodacc.webapi.models.Vehicle
import java.io.Serializable
import java.util.*

@Entity
@Table(
    name = "product_vehicle_reference",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_vehicle",
            columnNames = ["product_id", "vehicle_id"]
        )
    ]
)
@IdClass(ProductVehicleReferenceId::class)
data class ProductVehicleReference(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    val productId: Product,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "vehicle_id", nullable = false)
    val vehicleId: ProductVehicle
) {
    internal constructor(): this(
        productId = Product(),
        vehicleId = ProductVehicle()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductVehicleReference) return false
        return productId.id == other.productId.id &&
                vehicleId.id == other.vehicleId.id
    }

    override fun hashCode(): Int {
        return Objects.hash(productId.id, vehicleId.id)
    }

    override fun toString(): String =
        "ProductVehicleReference(productId=${productId.id}, vehicleId=${vehicleId.id})"

}


data class ProductVehicleReferenceId(
    val productId: UUID? = null,
    val vehicleId: UUID? = null
) : Serializable