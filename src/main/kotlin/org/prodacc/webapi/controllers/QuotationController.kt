package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.QuotationStatus
import org.prodacc.webapi.services.QuotationService
import org.prodacc.webapi.services.dataTransferObjects.CreateQuotationDto
import org.prodacc.webapi.services.dataTransferObjects.QuotationResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
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
@RequestMapping("/api/quotations")
@CrossOrigin(origins = ["*"])
class QuotationController(
    private val quotationService: QuotationService
) {

    @GetMapping("/all")
    fun getAllQuotations(): ResponseEntity<List<QuotationResponseDto>> =
        ResponseEntity.ok(quotationService.getAllQuotations())

    @GetMapping("/{id}")
    fun getQuotation(@PathVariable id: UUID): ResponseEntity<QuotationResponseDto> =
        try {
            ResponseEntity.ok(quotationService.getQuotationById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/new")
    fun createQuotation(@RequestBody createDto: CreateQuotationDto): ResponseEntity<QuotationResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(quotationService.createQuotation(createDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    @PutMapping("/{id}/status")
    fun updateQuotationStatus(
        @PathVariable id: UUID,
        @RequestParam status: QuotationStatus
    ): ResponseEntity<QuotationResponseDto> =
        try {
            ResponseEntity.ok(quotationService.updateQuotationStatus(id, status))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/{quotationId}/convert-to-job-card")
    fun convertToJobCard(
        @PathVariable quotationId: UUID,
        @RequestParam jobCardId: UUID
    ): ResponseEntity<QuotationResponseDto> =
        try {
            ResponseEntity.ok(quotationService.convertToJobCard(quotationId, jobCardId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
}
