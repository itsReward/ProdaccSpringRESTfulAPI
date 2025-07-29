package org.prodacc.webapi.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.ProductVehicleReferenceService
import java.util.*

// DTOs
data class CreateReferenceDto(
    val productId: UUID,
    val vehicleId: UUID
)

data class BatchCreateReferenceDto(
    val references: List<CreateReferenceDto>
)

data class UpdateProductReferencesDto(
    val vehicleIds: List<UUID>
)

data class UpdateVehicleReferencesDto(
    val productIds: List<UUID>
)

data class ProductVehicleReferenceResponseDto(
    val productId: UUID,
    val vehicleId: UUID
)

@RestController
@RequestMapping("/api/product-vehicle-references")
@CrossOrigin(origins = ["*"])
class ProductVehicleReferenceController(
    private val productVehicleReferenceService: ProductVehicleReferenceService
) {

    @PostMapping("/new")
    fun createReference(@RequestBody createDto: CreateReferenceDto): ResponseEntity<ProductVehicleReferenceResponseDto> =
        try {
            val reference = productVehicleReferenceService.createReference(createDto.productId, createDto.vehicleId)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductVehicleReferenceResponseDto(reference.productId, reference.vehicleId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PostMapping("/batch/new")
    fun createReferences(@RequestBody batchDto: BatchCreateReferenceDto): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val references = batchDto.references.map { it.productId to it.vehicleId }
            val created = productVehicleReferenceService.createReferences(references)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(created.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @GetMapping("/product/{productId}/vehicles")
    fun getVehiclesByProduct(@PathVariable productId: UUID): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val references = productVehicleReferenceService.findVehiclesByProduct(productId)
            ResponseEntity.ok(references.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/vehicle/{vehicleId}/products")
    fun getProductsByVehicle(@PathVariable vehicleId: UUID): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val references = productVehicleReferenceService.findProductsByVehicle(vehicleId)
            ResponseEntity.ok(references.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/products/vehicles")
    fun getVehiclesByProducts(@RequestBody productIds: List<UUID>): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val references = productVehicleReferenceService.findVehiclesByProducts(productIds)
            ResponseEntity.ok(references.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PostMapping("/vehicles/products")
    fun getProductsByVehicles(@RequestBody vehicleIds: List<UUID>): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val references = productVehicleReferenceService.findProductsByVehicles(vehicleIds)
            ResponseEntity.ok(references.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @GetMapping("/exists")
    fun checkReferenceExists(
        @RequestParam productId: UUID,
        @RequestParam vehicleId: UUID
    ): ResponseEntity<Boolean> =
        try {
            ResponseEntity.ok(productVehicleReferenceService.referenceExists(productId, vehicleId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @DeleteMapping("/delete")
    fun deleteReference(
        @RequestParam productId: UUID,
        @RequestParam vehicleId: UUID
    ): ResponseEntity<String> =
        try {
            val deleted = productVehicleReferenceService.deleteReference(productId, vehicleId)
            if (deleted) {
                ResponseEntity.ok("Reference deleted successfully")
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/product/{productId}/delete")
    fun deleteReferencesByProduct(@PathVariable productId: UUID): ResponseEntity<String> =
        try {
            val deletedCount = productVehicleReferenceService.deleteReferencesByProduct(productId)
            ResponseEntity.ok("$deletedCount references deleted for product")
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/vehicle/{vehicleId}/delete")
    fun deleteReferencesByVehicle(@PathVariable vehicleId: UUID): ResponseEntity<String> =
        try {
            val deletedCount = productVehicleReferenceService.deleteReferencesByVehicle(vehicleId)
            ResponseEntity.ok("$deletedCount references deleted for vehicle")
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PutMapping("/product/{productId}/vehicles/update")
    fun updateProductVehicleReferences(
        @PathVariable productId: UUID,
        @RequestBody updateDto: UpdateProductReferencesDto
    ): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val updated = productVehicleReferenceService.updateProductVehicleReferences(productId, updateDto.vehicleIds)
            ResponseEntity.ok(updated.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PutMapping("/vehicle/{vehicleId}/products/update")
    fun updateVehicleProductReferences(
        @PathVariable vehicleId: UUID,
        @RequestBody updateDto: UpdateVehicleReferencesDto
    ): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        try {
            val updated = productVehicleReferenceService.updateVehicleProductReferences(vehicleId, updateDto.productIds)
            ResponseEntity.ok(updated.map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) })
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/all")
    fun getAllReferences(): ResponseEntity<List<ProductVehicleReferenceResponseDto>> =
        ResponseEntity.ok(
            productVehicleReferenceService.findAll()
                .map { ProductVehicleReferenceResponseDto(it.productId, it.vehicleId) }
        )

    @GetMapping("/count")
    fun getReferencesCount(): ResponseEntity<Long> =
        ResponseEntity.ok(productVehicleReferenceService.countReferences())
}