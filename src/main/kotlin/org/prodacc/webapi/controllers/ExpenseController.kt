package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.ExpenseService
import org.prodacc.webapi.services.dataTransferObjects.CreateExpenseCategoryDto
import org.prodacc.webapi.services.dataTransferObjects.CreateExpenseDto
import org.prodacc.webapi.services.dataTransferObjects.ExpenseCategoryResponseDto
import org.prodacc.webapi.services.dataTransferObjects.ExpenseResponseDto
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
@RequestMapping("/expenses")
@CrossOrigin(origins = ["*"])
class ExpenseController(
    private val expenseService: ExpenseService
) {

    @GetMapping("/all")
    fun getAllExpenses(): ResponseEntity<List<ExpenseResponseDto>> =
        ResponseEntity.ok(expenseService.getAllExpenses())

    @GetMapping("/{id}")
    fun getExpense(@PathVariable id: UUID): ResponseEntity<ExpenseResponseDto> =
        try {
            ResponseEntity.ok(expenseService.getExpenseById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createExpense(@RequestBody createDto: CreateExpenseDto): ResponseEntity<ExpenseResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.createExpense(createDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/update/{id}")
    fun updateExpense(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateExpenseDto
    ): ResponseEntity<ExpenseResponseDto> =
        try {
            ResponseEntity.ok(expenseService.updateExpense(id, updateDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/delete/{id}")
    fun deleteExpense(@PathVariable id: UUID): ResponseEntity<String> =
        try {
            ResponseEntity.ok(expenseService.deleteExpense(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @GetMapping("/categories")
    fun getAllExpenseCategories(): ResponseEntity<List<ExpenseCategoryResponseDto>> =
        ResponseEntity.ok(expenseService.getAllCategories())

    @PostMapping("/categories/new")
    fun createExpenseCategory(@RequestBody createDto: CreateExpenseCategoryDto): ResponseEntity<ExpenseCategoryResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.createCategory(createDto))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
}
