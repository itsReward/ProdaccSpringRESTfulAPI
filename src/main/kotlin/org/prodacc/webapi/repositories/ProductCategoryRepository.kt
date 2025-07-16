package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, UUID> {
    fun findByCategoryNameContainingIgnoreCase(categoryName: String): List<ProductCategory>
    fun existsByCategoryNameIgnoreCase(categoryName: String): Boolean
}
