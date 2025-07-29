package org.prodacc.webapi.services

import org.prodacc.webapi.models.ProductVehicleReference
import org.prodacc.webapi.repositories.ProductVehicleReferenceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.map

@Service
@Transactional
class ProductVehicleReferenceService(
    private val repository: ProductVehicleReferenceRepository
) {

    /**
     * Creates a new product-vehicle reference relationship
     */
    fun createReference(productId: UUID, vehicleId: UUID): ProductVehicleReference {
        if (repository.existsByProductIdAndVehicleId(productId, vehicleId)) {
            throw IllegalArgumentException("Reference already exists between product $productId and vehicle $vehicleId")
        }

        val reference = ProductVehicleReference(
            productId = productId,
            vehicleId = vehicleId
        )

        return repository.save(reference)
    }

    /**
     * Creates multiple product-vehicle references
     */
    fun createReferences(references: List<Pair<UUID, UUID>>): List<ProductVehicleReference> {
        val entities = references.map { (productId, vehicleId) ->
            ProductVehicleReference(productId = productId, vehicleId = vehicleId)
        }
        return repository.saveAll(entities)
    }

    /**
     * Finds all vehicles associated with a product
     */
    @Transactional(readOnly = true)
    fun findVehiclesByProduct(productId: UUID): List<ProductVehicleReference> {
        return repository.findByProductId(productId)
    }

    /**
     * Finds all products associated with a vehicle
     */
    @Transactional(readOnly = true)
    fun findProductsByVehicle(vehicleId: UUID): List<ProductVehicleReference> {
        return repository.findByVehicleId(vehicleId)
    }

    /**
     * Finds all references for multiple products
     */
    @Transactional(readOnly = true)
    fun findVehiclesByProducts(productIds: List<UUID>): List<ProductVehicleReference> {
        return repository.findByProductIds(productIds)
    }

    /**
     * Finds all references for multiple vehicles
     */
    @Transactional(readOnly = true)
    fun findProductsByVehicles(vehicleIds: List<UUID>): List<ProductVehicleReference> {
        return repository.findByVehicleIds(vehicleIds)
    }

    /**
     * Checks if a reference exists between a product and vehicle
     */
    @Transactional(readOnly = true)
    fun referenceExists(productId: UUID, vehicleId: UUID): Boolean {
        return repository.existsByProductIdAndVehicleId(productId, vehicleId)
    }

    /**
     * Deletes a specific product-vehicle reference
     */
    fun deleteReference(productId: UUID, vehicleId: UUID): Boolean {
        val deletedCount = repository.deleteByProductIdAndVehicleId(productId, vehicleId)
        return deletedCount > 0
    }

    /**
     * Deletes all references for a specific product
     */
    fun deleteReferencesByProduct(productId: UUID): Int {
        val references = repository.findByProductId(productId)
        repository.deleteAll(references)
        return references.size
    }

    /**
     * Deletes all references for a specific vehicle
     */
    fun deleteReferencesByVehicle(vehicleId: UUID): Int {
        val references = repository.findByVehicleId(vehicleId)
        repository.deleteAll(references)
        return references.size
    }

    /**
     * Updates vehicle references for a product (replaces all existing references)
     */
    fun updateProductVehicleReferences(productId: UUID, vehicleIds: List<UUID>): List<ProductVehicleReference> {
        // Delete existing references
        deleteReferencesByProduct(productId)

        // Create new references
        val references = vehicleIds.map { vehicleId ->
            ProductVehicleReference(productId = productId, vehicleId = vehicleId)
        }

        return repository.saveAll(references)
    }

    /**
     * Updates product references for a vehicle (replaces all existing references)
     */
    fun updateVehicleProductReferences(vehicleId: UUID, productIds: List<UUID>): List<ProductVehicleReference> {
        // Delete existing references
        deleteReferencesByVehicle(vehicleId)

        // Create new references
        val references = productIds.map { productId ->
            ProductVehicleReference(productId = productId, vehicleId = vehicleId)
        }

        return repository.saveAll(references)
    }

    /**
     * Gets all references with pagination support
     */
    @Transactional(readOnly = true)
    fun findAll(): List<ProductVehicleReference> {
        return repository.findAll()
    }

    /**
     * Counts total number of references
     */
    @Transactional(readOnly = true)
    fun countReferences(): Long {
        return repository.count()
    }
}