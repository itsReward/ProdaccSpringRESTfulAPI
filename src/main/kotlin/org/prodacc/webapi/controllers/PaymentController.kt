package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.services.PaymentService
import org.prodacc.webapi.services.dataTransferObjects.CreatePaymentDto
import org.prodacc.webapi.services.dataTransferObjects.PaymentResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = ["*"])
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/process")
    fun processPayment(@RequestBody createDto: CreatePaymentDto): ResponseEntity<PaymentResponseDto> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(createDto))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @GetMapping("/invoice/{invoiceId}")
    fun getInvoicePayments(@PathVariable invoiceId: UUID): ResponseEntity<List<PaymentResponseDto>> =
        ResponseEntity.ok(paymentService.getInvoicePayments(invoiceId))

    @PostMapping("/refund/{paymentId}")
    fun refundPayment(@PathVariable paymentId: UUID): ResponseEntity<PaymentResponseDto> =
        try {
            ResponseEntity.ok(paymentService.refundPayment(paymentId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
}

