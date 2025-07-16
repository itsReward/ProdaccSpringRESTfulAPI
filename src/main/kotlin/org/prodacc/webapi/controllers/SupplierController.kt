package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.SupplierService
import org.prodacc.webapi.services.dataTransferObjects.CreateSupplierDto
import org.prodacc.webapi.services.dataTransferObjects.SupplierResponseDto
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = ["*"])
class SupplierController(
    private val supplierService: SupplierService
) {

    @GetMapping("/all")
    fun getAllSuppliers(): ResponseEntity<List<SupplierResponseDto>> =
        ResponseEntity.ok(supplierService.getAllSuppliers())

    @GetMapping("/{id}")
    fun getSupplier(@PathVariable id: UUID): ResponseEntity<SupplierResponseDto> =
        try {
            ResponseEntity.ok(supplierService.getSupplierById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createSupplier(@RequestBody createDto: CreateSupplierDto): ResponseEntity<SupplierResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(createDto))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/update/{id}")
    fun updateSupplier(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateSupplierDto
    ): ResponseEntity<SupplierResponseDto> =
        try {
            ResponseEntity.ok(supplierService.updateSupplier(id, updateDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/delete/{id}")
    fun deleteSupplier(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(supplierService.deleteSupplier(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/search")
    fun searchSuppliers(@RequestParam searchTerm: String): ResponseEntity<List<SupplierResponseDto>> =
        ResponseEntity.ok(supplierService.searchSuppliers(searchTerm))
}
