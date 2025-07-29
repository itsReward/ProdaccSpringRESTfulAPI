package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.repositories.ProductVehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateProductVehicleDto
import org.prodacc.webapi.services.dataTransferObjects.ProductVehicleResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.prodacc.webapi.services.dataTransferObjects.toEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class ProductVehicleService(
    private val productVehicleRepository: ProductVehicleRepository,
) {
    private val logger = LoggerFactory.getLogger(ProductVehicleService::class.java)

    fun getAllVehicles(): List<ProductVehicleResponseDto> =
        productVehicleRepository.findAll().map { it.toDto() }

    fun getVehicleById(id: UUID): ProductVehicleResponseDto {
        val vehicle = productVehicleRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product vehicle not found with ID: $id") }
        return vehicle.toDto()
    }

    fun createVehicle(createDto: CreateProductVehicleDto): ProductVehicleResponseDto {
        if (productVehicleRepository.findProductVehicleByVehicleMakeIgnoreCaseAndVehicleModelIgnoreCaseAndYear(createDto.vehicleMake, createDto.vehicleModel, createDto.year)) {
            throw IllegalArgumentException("Vehicle with name '${createDto.vehicleMake}' already exists")
        }
        val category = createDto.toEntity()
        val savedCategory = productVehicleRepository.save(category)
        return savedCategory.toDto()
    }

    fun updateVehicle(id: UUID, updateDto: CreateProductVehicleDto): ProductVehicleResponseDto {
        val existingVehicle = productVehicleRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product vehicle not found with ID: $id") }

        val updatedVehicle = existingVehicle.copy(
            vehicleMake = updateDto.vehicleMake,
            vehicleModel = updateDto.vehicleMake,
            year = updateDto.year,
        )
        val savedVehicle= productVehicleRepository.save(updatedVehicle)
        return savedVehicle.toDto()
    }

    fun deleteVehicle(id: UUID): String {
        if (!productVehicleRepository.existsById(id)) {
            throw EntityNotFoundException("Product vehicle not found with ID: $id")
        }
        productVehicleRepository.deleteById(id)
        return "vehicle successfully deleted"
    }
}