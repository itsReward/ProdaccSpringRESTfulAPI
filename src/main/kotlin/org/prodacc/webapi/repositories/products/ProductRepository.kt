package org.prodacc.webapi.repositories.products

import jakarta.persistence.LockModeType
import org.prodacc.webapi.models.products.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.*

@Deprecated("Repo deprecated use recent one")
interface ProductRepository: JpaRepository<Product, UUID> {
    override fun findById(id: UUID): Optional<Product>

    @Query("SELECT p FROM Product p")
    override fun findAll(): List<Product>

    @Query("""
        SELECT p FROM Product p 
        LEFT JOIN FETCH p.productCategory pc 
        LEFT JOIN FETCH pc.categoryId
        LEFT JOIN FETCH p.productVehicle pv 
        LEFT JOIN FETCH pv.vehicleId 
        WHERE p.id = :id
    """)
    fun findByIdWithRelationships(id: UUID): Optional<Product>

    @Query("""
        SELECT DISTINCT p FROM Product p 
        JOIN p.productCategory pc 
        WHERE pc.categoryId = :categoryId
    """)
    fun findProductsByCategoryId(categoryId: UUID): List<Product>

    @Query("""
        SELECT DISTINCT p FROM Product p 
        JOIN p.productVehicle pv 
        WHERE pv.vehicleId = :vehicleId
    """)
    fun findProductsByVehicleId(vehicleId: UUID): List<Product>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun <S : Product> save(entity: S): S

}