package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.repositories.SupplierRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateSupplierDto
import org.prodacc.webapi.services.dataTransferObjects.SupplierResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.prodacc.webapi.services.dataTransferObjects.toEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class SupplierService(
    private val supplierRepository: SupplierRepository
) {
    private val logger = LoggerFactory.getLogger(SupplierService::class.java)

    fun getAllSuppliers(): List<SupplierResponseDto> {
        logger.info("Fetching all suppliers")
        return supplierRepository.findByIsActiveTrue().map { it.toDto() }
    }

    fun getSupplierById(id: UUID): SupplierResponseDto {
        logger.info("Fetching supplier with ID: $id")
        val supplier = supplierRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Supplier not found with ID: $id") }
        return supplier.toDto()
    }

    fun createSupplier(createDto: CreateSupplierDto): SupplierResponseDto {
        logger.info("Creating new supplier: ${createDto.supplierName}")

        if (supplierRepository.existsBySupplierNameIgnoreCase(createDto.supplierName)) {
            throw IllegalArgumentException("Supplier with name '${createDto.supplierName}' already exists")
        }

        val supplier = createDto.toEntity()
        val savedSupplier = supplierRepository.save(supplier)
        logger.info("Successfully created supplier with ID: ${savedSupplier.supplierId}")

        return savedSupplier.toDto()
    }

    fun updateSupplier(id: UUID, updateDto: CreateSupplierDto): SupplierResponseDto {
        logger.info("Updating supplier with ID: $id")

        val existingSupplier = supplierRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Supplier not found with ID: $id") }

        val updatedSupplier = existingSupplier.copy(
            supplierName = updateDto.supplierName,
            companyName = updateDto.companyName,
            contactPerson = updateDto.contactPerson,
            email = updateDto.email,
            phone = updateDto.phone,
            address = updateDto.address,
            paymentTerms = updateDto.paymentTerms,
            taxNumber = updateDto.taxNumber,
            updatedAt = LocalDateTime.now()
        )

        val savedSupplier = supplierRepository.save(updatedSupplier)
        logger.info("Successfully updated supplier with ID: $id")

        return savedSupplier.toDto()
    }

    fun deleteSupplier(id: UUID): String {
        logger.info("Deleting supplier with ID: $id")

        val supplier = supplierRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Supplier not found with ID: $id") }

        // Soft delete by setting isActive to false
        val deactivatedSupplier = supplier.copy(isActive = false, updatedAt = LocalDateTime.now())
        supplierRepository.save(deactivatedSupplier)

        logger.info("Successfully deactivated supplier with ID: $id")
        return "Supplier successfully deactivated"
    }

    fun searchSuppliers(searchTerm: String): List<SupplierResponseDto> {
        logger.info("Searching suppliers with term: $searchTerm")
        return supplierRepository.findBySupplierNameContainingIgnoreCase(searchTerm).map { it.toDto() }
    }
}