package org.prodacc.webapi.repositories.products

import org.prodacc.webapi.models.products.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

@Deprecated("Use the latest ProductCategoryRepository")
interface ProductCategoryRepository: JpaRepository<ProductCategory, UUID> {
    fun findByName(name: String): Optional<ProductCategory>

    @Query("""
        SELECT DISTINCT pc FROM ProductCategory pc 
        LEFT JOIN FETCH pc.productReference 
        WHERE pc.id = :id
    """)
    fun findByIdWithProducts(id: UUID): Optional<ProductCategory>

    @Query("""
        SELECT DISTINCT pc FROM ProductCategory pc 
        LEFT JOIN FETCH pc.productReference
    """)
    fun findAllWithProducts(): List<ProductCategory>
}