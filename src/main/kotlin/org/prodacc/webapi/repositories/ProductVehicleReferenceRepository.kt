package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ProductVehicleReference
import org.prodacc.webapi.models.ProductVehicleReferenceId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductVehicleReferenceRepository : JpaRepository<ProductVehicleReference,ProductVehicleReferenceId> {

    fun findByProductId(productId: UUID): List<ProductVehicleReference>

    fun findByVehicleId(vehicleId: UUID): List<ProductVehicleReference>

    fun existsByProductIdAndVehicleId(productId: UUID, vehicleId: UUID): Boolean

    fun deleteByProductIdAndVehicleId(productId: UUID, vehicleId: UUID): Int

    @Query("SELECT pvr FROM ProductVehicleReference pvr WHERE pvr.productId IN :productIds")
    fun findByProductIds(@Param("productIds") productIds: List<UUID>): List<ProductVehicleReference>

    @Query("SELECT pvr FROM ProductVehicleReference pvr WHERE pvr.vehicleId IN :vehicleIds")
    fun findByVehicleIds(@Param("vehicleIds") vehicleIds: List<UUID>): List<ProductVehicleReference>
}