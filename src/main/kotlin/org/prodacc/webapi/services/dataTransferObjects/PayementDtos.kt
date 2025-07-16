package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.Payment
import org.prodacc.webapi.models.PaymentMethod
import org.prodacc.webapi.models.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreatePaymentDto(
    val invoiceId: UUID,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val transactionReference: String? = null,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val processedBy: UUID? = null
)

data class PaymentResponseDto(
    val paymentId: UUID,
    val invoiceNumber: String,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val transactionReference: String?,
    val paymentDate: LocalDateTime,
    val referenceNumber: String?,
    val notes: String?,
    val processedBy: UUID?
)

fun Payment.toDto(): PaymentResponseDto = PaymentResponseDto(
    paymentId = this.paymentId!!,
    invoiceNumber = this.invoice.invoiceNumber,
    amount = this.amount,
    paymentMethod = this.paymentMethod,
    paymentStatus = this.paymentStatus,
    transactionReference = this.transactionReference,
    paymentDate = this.paymentDate,
    referenceNumber = this.referenceNumber,
    notes = this.notes,
    processedBy = this.processedBy
)