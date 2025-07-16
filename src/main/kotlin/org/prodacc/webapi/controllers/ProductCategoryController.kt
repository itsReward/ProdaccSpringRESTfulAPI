package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.ProductCategoryService
import org.prodacc.webapi.services.dataTransferObjects.CreateProductCategoryDto
import org.prodacc.webapi.services.dataTransferObjects.ProductCategoryResponseDto
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
@RequestMapping("/api/product-categories")
@CrossOrigin(origins = ["*"])
class ProductCategoryController(
    private val productCategoryService: ProductCategoryService
) {

    @GetMapping("/all")
    fun getAllCategories(): ResponseEntity<List<ProductCategoryResponseDto>> =
        ResponseEntity.ok(productCategoryService.getAllCategories())

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: UUID): ResponseEntity<ProductCategoryResponseDto> =
        try {
            ResponseEntity.ok(productCategoryService.getCategoryById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createCategory(@RequestBody createDto: CreateProductCategoryDto): ResponseEntity<ProductCategoryResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(productCategoryService.createCategory(createDto))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/update/{id}")
    fun updateCategory(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateProductCategoryDto
    ): ResponseEntity<ProductCategoryResponseDto> =
        try {
            ResponseEntity.ok(productCategoryService.updateCategory(id, updateDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/delete/{id}")
    fun deleteCategory(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(productCategoryService.deleteCategory(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
}
