package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Supplier
import java.time.LocalDateTime
import java.util.*

// ===== SUPPLIER DTOs =====
data class CreateSupplierDto(
    val supplierName: String,
    val companyName: String? = null,
    val contactPerson: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentTerms: String? = null,
    val taxNumber: String? = null
)

data class SupplierResponseDto(
    val supplierId: UUID,
    val supplierName: String,
    val companyName: String?,
    val contactPerson: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val paymentTerms: String?,
    val taxNumber: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

fun Supplier.toDto(): SupplierResponseDto = SupplierResponseDto(
    supplierId = this.supplierId!!,
    supplierName = this.supplierName,
    companyName = this.companyName,
    contactPerson = this.contactPerson,
    email = this.email,
    phone = this.phone,
    address = this.address,
    paymentTerms = this.paymentTerms,
    taxNumber = this.taxNumber,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun CreateSupplierDto.toEntity(): Supplier = Supplier(
    supplierName = this.supplierName,
    companyName = this.companyName,
    contactPerson = this.contactPerson,
    email = this.email,
    phone = this.phone,
    address = this.address,
    paymentTerms = this.paymentTerms,
    taxNumber = this.taxNumber
)

