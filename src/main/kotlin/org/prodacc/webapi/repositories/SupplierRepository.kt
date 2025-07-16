package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Supplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SupplierRepository : JpaRepository<Supplier, UUID> {
    fun findByIsActiveTrue(): List<Supplier>
    fun findBySupplierNameContainingIgnoreCase(supplierName: String): List<Supplier>
    fun findByEmailIgnoreCase(email: String): Supplier?
    fun existsBySupplierNameIgnoreCase(supplierName: String): Boolean
}