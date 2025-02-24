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
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    // Product CRUD Operations
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.getProductById(id).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/{id}/full")
    fun getProductWithRelationships(@PathVariable id: UUID): ResponseEntity<ProductResponseDto> =
        try {
            ResponseEntity.ok(productService.getProductWithRelationships(id).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getAllProducts().map { it.toDto() })

    @PostMapping("/new")
    fun createProduct(@RequestBody product: CreateProductDto): ResponseEntity<ProductResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(product).toDto())

    @PutMapping("/update/{id}")
    fun updateProduct(
        @PathVariable id: UUID,
        @RequestBody product: CreateProductDto
    ): ResponseEntity<ProductResponseDto> {
        return try {
            ResponseEntity.ok(productService.updateProduct(id, product).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteProduct(@PathVariable id: UUID): ResponseEntity<Unit> =
        try {
            productService.deleteProduct(id)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    // Category CRUD Operations
    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<ProductCategoryResponseDto>> =
        ResponseEntity.ok(productService.getAllCategories().map { it.toDto() })

    @GetMapping("/categories/with-products")
    fun getAllCategoriesWithProducts(): ResponseEntity<List<ProductCategoryWithProductsResponseDto>> =
        ResponseEntity.ok(productService.getAllCategoriesWithProducts().map { it.toDtoWithProducts() })

    @GetMapping("/categories/{categoryId}")
    fun getCategoryById(@PathVariable categoryId: UUID): ResponseEntity<ProductCategoryResponseDto> =
        try {
            ResponseEntity.ok(productService.getCategoryById(categoryId).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/categories/new-category")
    fun createCategory(@RequestBody category: CreateProductCategoryDto): ResponseEntity<ProductCategoryResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createCategory(category).toDto())

    @PutMapping("/categories/update-category/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: UUID,
        @RequestBody category: CreateProductCategoryDto
    ): ResponseEntity<ProductCategoryResponseDto> {
        return try {
            ResponseEntity.ok(productService.updateCategory(categoryId, category).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/categories/delete/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: UUID): ResponseEntity<Unit> =
        try {
            productService.deleteCategory(categoryId)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/categories/{categoryId}/with-products")
    fun getCategoryWithProducts(@PathVariable categoryId: UUID): ResponseEntity<ProductCategoryWithProductsResponseDto> =
        try {
            ResponseEntity.ok(productService.getCategoryWithProducts(categoryId).toDtoWithProducts())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/by-category/{categoryId}") //TODO: fix this
    fun getProductsByCategory(@PathVariable categoryId: UUID): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getProductsByCategoryId(categoryId).map { it.toDto() })

    // Vehicle CRUD Operations
    @GetMapping("/vehicles")
    fun getAllVehicles(): ResponseEntity<List<ProductVehicleResponseDto>> =
        ResponseEntity.ok(productService.getAllVehicles().map { it.toDto() })

    @GetMapping("/vehicles/with-products")
    fun getAllVehiclesWithProducts(): ResponseEntity<List<ProductVehicleWithProductsResponseDto>> =
        ResponseEntity.ok(productService.getAllVehiclesWithProducts().map { it.toDtoWithProducts() })

    @GetMapping("/vehicles/{vehicleId}")
    fun getVehicleById(@PathVariable vehicleId: UUID): ResponseEntity<ProductVehicleResponseDto> =
        try {
            ResponseEntity.ok(productService.getVehicleById(vehicleId).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/vehicles/new-vehicle")
    fun createVehicle(@RequestBody createVehicleDto: CreateProductVehicleDto): ResponseEntity<ProductVehicleResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createVehicle(createVehicleDto).toDto())

    @PutMapping("/vehicles/{vehicleId}")
    fun updateVehicle(
        @PathVariable vehicleId: UUID,
        @RequestBody vehicle: CreateProductVehicleDto
    ): ResponseEntity<ProductVehicleResponseDto> {
        return try {
            ResponseEntity.ok(productService.updateVehicle(vehicleId, vehicle).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    fun deleteVehicle(@PathVariable vehicleId: UUID): ResponseEntity<Unit> =
        try {
            productService.deleteVehicle(vehicleId)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/vehicles/{vehicleId}/with-products")
    fun getVehicleWithProducts(@PathVariable vehicleId: UUID): ResponseEntity<ProductVehicleResponseDto> =
        try {
            ResponseEntity.ok(productService.getVehicleWithProducts(vehicleId).toDto())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/vehicles/search")
    fun getVehiclesByMakeAndModel(
        @RequestParam make: String,
        @RequestParam model: String
    ): ResponseEntity<List<ProductVehicleResponseDto>> =
        ResponseEntity.ok(productService.getVehiclesByMakeAndModel(make, model).map { it.toDto() })

    @GetMapping("/by-vehicle/{vehicleId}") //TODO() fix this
    fun getProductsByVehicle(@PathVariable vehicleId: UUID): ResponseEntity<List<ProductResponseDto>> =
        ResponseEntity.ok(productService.getProductsByVehicleId(vehicleId).map { it.toDto() })

    // Relationship Management Operations
    @PostMapping("/{productId}/categories/{categoryId}")
    fun addCategoryToProduct(
        @PathVariable productId: UUID,
        @PathVariable categoryId: UUID
    ): ResponseEntity<Unit> =
        try {
            productService.addCategoryToProduct(productId, categoryId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    fun removeCategoryFromProduct(
        @PathVariable productId: UUID,
        @PathVariable categoryId: UUID
    ): ResponseEntity<Unit> =
        try {
            productService.removeCategoryFromProduct(productId, categoryId)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/{productId}/vehicles/{vehicleId}")
    fun addVehicleToProduct(
        @PathVariable productId: UUID,
        @PathVariable vehicleId: UUID
    ): ResponseEntity<Unit> =
        try {
            productService.addVehicleToProduct(productId, vehicleId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/{productId}/vehicles/{vehicleId}")
    fun removeVehicleFromProduct(
        @PathVariable productId: UUID,
        @PathVariable vehicleId: UUID
    ): ResponseEntity<Unit> =
        try {
            productService.removeVehicleFromProduct(productId, vehicleId)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    // Error Handling
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity.badRequest().body(e.message)

    @ExceptionHandler(Exception::class)
    fun handleGenericError(e: Exception): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred")
}