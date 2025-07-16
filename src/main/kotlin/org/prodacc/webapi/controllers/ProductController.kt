package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.products.Product
import org.prodacc.webapi.services.ProductService
import org.prodacc.webapi.services.dataTransferObjects.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["*"])
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/all")
    fun getAllProducts(): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getAllProducts())

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.getProductById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/{id}/full")
    fun getProductWithRelationships(@PathVariable id: UUID): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.getProductWithRelationships(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createProduct(@RequestBody createDto: CreateProductDto): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(createDto))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/update/{id}")
    fun updateProduct(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateProductDto
    ): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.updateProduct(id, updateDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/delete/{id}")
    fun deleteProduct(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(productService.deleteProduct(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/low-stock")
    fun getLowStockProducts(): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getLowStockProducts())

    @GetMapping("/vehicle-compatibility")
    fun getProductsForVehicle(
        @RequestParam make: String,
        @RequestParam(required = false) model: String?
    ): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getProductsForVehicle(make, model))

    @PutMapping("/{id}/stock")
    fun updateStock(
        @PathVariable id: UUID,
        @RequestParam newStock: Int,
        @RequestParam(required = false, defaultValue = "Manual adjustment") reason: String
    ): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.updateStock(id, newStock, reason))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
}