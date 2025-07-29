package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.InventoryTransactionService
import org.prodacc.webapi.services.dataTransferObjects.CreateInventoryTransactionDto
import org.prodacc.webapi.services.dataTransferObjects.InventoryTransactionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/inventory-transactions")
@CrossOrigin(origins = ["*"])
class InventoryTransactionController(
    private val inventoryTransactionService: InventoryTransactionService
) {

    @PostMapping("/new")
    fun createTransaction(@RequestBody createDto: CreateInventoryTransactionDto): ResponseEntity<InventoryTransactionResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryTransactionService.createTransaction(createDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    @GetMapping("/product/{productId}")
    fun getProductTransactions(@PathVariable productId: UUID): ResponseEntity<List<InventoryTransactionResponseDto>> =
        try {
            ResponseEntity.ok(inventoryTransactionService.getTransactionsByProduct(productId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/recent")
    fun getRecentTransactions(@RequestParam(defaultValue = "50") limit: Int): ResponseEntity<List<InventoryTransactionResponseDto>> =
        ResponseEntity.ok(inventoryTransactionService.getRecentTransactions(limit))
}