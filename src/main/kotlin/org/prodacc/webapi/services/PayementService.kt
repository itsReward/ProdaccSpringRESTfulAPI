package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.InvoiceStatus
import org.prodacc.webapi.models.Payment
import org.prodacc.webapi.models.PaymentStatus
import org.prodacc.webapi.repositories.InvoiceRepository
import org.prodacc.webapi.repositories.PaymentRepository
import org.prodacc.webapi.services.dataTransferObjects.CreatePaymentDto
import org.prodacc.webapi.services.dataTransferObjects.PaymentResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val invoiceRepository: InvoiceRepository
) {
    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    fun processPayment(createDto: CreatePaymentDto): PaymentResponseDto {
        logger.info("Processing payment for invoice: ${createDto.invoiceId}")

        val invoice = invoiceRepository.findById(createDto.invoiceId)
            .orElseThrow { EntityNotFoundException("Invoice not found with ID: ${createDto.invoiceId}") }

        if (createDto.amount > invoice.balanceDue) {
            throw IllegalArgumentException("Payment amount cannot exceed balance due")
        }

        val payment = Payment(
            invoice = invoice,
            amount = createDto.amount,
            paymentMethod = createDto.paymentMethod,
            transactionReference = createDto.transactionReference,
            referenceNumber = createDto.referenceNumber,
            notes = createDto.notes,
            processedBy = createDto.processedBy,
            paymentStatus = PaymentStatus.COMPLETED
        )

        val savedPayment = paymentRepository.save(payment)

        // Update invoice payment status
        updateInvoicePaymentStatus(invoice, createDto.amount)

        logger.info("Successfully processed payment with ID: ${savedPayment.paymentId}")
        return savedPayment.toDto()
    }

    fun getInvoicePayments(invoiceId: UUID): List<PaymentResponseDto> {
        logger.info("Fetching payments for invoice: $invoiceId")
        return paymentRepository.findByInvoiceOrderByDateDesc(invoiceId).map { it.toDto() }
    }

    fun refundPayment(paymentId: UUID): PaymentResponseDto {
        logger.info("Processing refund for payment: $paymentId")

        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { EntityNotFoundException("Payment not found with ID: $paymentId") }

        if (payment.paymentStatus != PaymentStatus.COMPLETED) {
            throw IllegalArgumentException("Can only refund completed payments")
        }

        val refundedPayment = payment.copy(
            paymentStatus = PaymentStatus.REFUNDED,
            updatedAt = LocalDateTime.now()
        )

        val savedPayment = paymentRepository.save(refundedPayment)

        // Update invoice payment status
        updateInvoicePaymentStatus(payment.invoice, payment.amount.negate())

        logger.info("Successfully processed refund for payment ID: $paymentId")
        return savedPayment.toDto()
    }

    private fun updateInvoicePaymentStatus(invoice: Invoice, paymentAmount: BigDecimal) {
        val newAmountPaid = invoice.amountPaid.add(paymentAmount)
        val newBalanceDue = invoice.totalAmount.subtract(newAmountPaid)

        val newStatus = when {
            newBalanceDue <= BigDecimal.ZERO -> InvoiceStatus.PAID
            newAmountPaid > BigDecimal.ZERO -> InvoiceStatus.PARTIALLY_PAID
            else -> invoice.status
        }

        val updatedInvoice = invoice.copy(
            amountPaid = newAmountPaid,
            balanceDue = newBalanceDue,
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )

        invoiceRepository.save(updatedInvoice)
    }
}