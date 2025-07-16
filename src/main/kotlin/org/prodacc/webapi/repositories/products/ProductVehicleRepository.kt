package org.prodacc.webapi.repositories.products

import org.prodacc.webapi.models.products.ProductVehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

@Deprecated("Use updated ProductVehicle Repository")
interface ProductVehicleRepository : JpaRepository<ProductVehicle, UUID> {
    fun findByMakeAndModel(make: String, model: String): List<ProductVehicle>



    @Query("""
        SELECT DISTINCT pv FROM ProductVehicle pv 
        LEFT JOIN FETCH pv.productReference 
        WHERE pv.id = :id
    """)
    fun findByIdWithProducts(id: UUID): Optional<ProductVehicle>

    @Query("""
        SELECT DISTINCT pv FROM ProductVehicle pv 
        LEFT JOIN FETCH pv.productReference
    """)
    fun findAllWithProducts(): List<ProductVehicle>

    @Query("""
        SELECT DISTINCT pv FROM ProductVehicle pv 
        LEFT JOIN FETCH pv.productReference 
        WHERE pv.make = :make AND pv.model = :model
    """)
    fun findByMakeAndModelWithProducts(make: String, model: String): List<ProductVehicle>

}