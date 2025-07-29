package org.prodacc.webapi.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.ProductVehicleService
import org.prodacc.webapi.services.dataTransferObjects.CreateProductVehicleDto
import org.prodacc.webapi.services.dataTransferObjects.ProductVehicleResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/product-vehicles")
@CrossOrigin(origins = ["*"])
@Tag(name = "product-vehicles-controller")
class ProductVehicleController(
    private val productVehicleService: ProductVehicleService
) {
    @GetMapping("/all")
    fun getAllVehicles(): ResponseEntity<List<ProductVehicleResponseDto>> =
        ResponseEntity.ok(productVehicleService.getAllVehicles())

    @GetMapping("/{id}")
    fun getVehicle(@PathVariable id: UUID): ResponseEntity<ProductVehicleResponseDto> =
        try {
            ResponseEntity.ok(productVehicleService.getVehicleById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createVehicle(@RequestBody createDto: CreateProductVehicleDto): ResponseEntity<ProductVehicleResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(productVehicleService.createVehicle(createDto))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/update/{id}")
    fun updateVehicle(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateProductVehicleDto
    ): ResponseEntity<ProductVehicleResponseDto> =
        try {
            ResponseEntity.ok(productVehicleService.updateVehicle(id, updateDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/delete/{id}")
    fun deleteVehicle(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(productVehicleService.deleteVehicle(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
}