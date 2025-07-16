package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.ItemType
import org.prodacc.webapi.models.Quotation
import org.prodacc.webapi.models.QuotationItem
import org.prodacc.webapi.models.QuotationStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class CreateQuotationDto(
    val clientId: UUID,
    val vehicleId: UUID? = null,
    val validUntil: LocalDate? = null,
    val taxRate: BigDecimal = BigDecimal("15.00"),
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val notes: String? = null,
    val termsAndConditions: String? = null,
    val items: List<CreateQuotationItemDto> = emptyList()
)

data class CreateQuotationItemDto(
    val productId: UUID? = null,
    val description: String,
    val quantity: BigDecimal = BigDecimal.ONE,
    val unitPrice: BigDecimal,
    val itemType: ItemType
)

data class QuotationResponseDto(
    val quotationId: UUID,
    val quotationNumber: String,
    val clientName: String,
    val clientSurname: String,
    val vehicleInfo: String?,
    val quotationDate: LocalDate,
    val validUntil: LocalDate?,
    val status: QuotationStatus,
    val subtotal: BigDecimal,
    val taxRate: BigDecimal,
    val taxAmount: BigDecimal,
    val discountPercentage: BigDecimal,
    val discountAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val notes: String?,
    val termsAndConditions: String?,
    val convertedToJobCard: Boolean,
    val createdAt: LocalDateTime,
    val items: List<QuotationItemResponseDto> = emptyList()
)

data class QuotationItemResponseDto(
    val itemId: UUID,
    val productCode: String?,
    val description: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val itemType: ItemType
)

fun Quotation.toDto(): QuotationResponseDto = QuotationResponseDto(
    quotationId = this.quotationId!!,
    quotationNumber = this.quotationNumber,
    clientName = this.client.clientName ?: "",
    clientSurname = this.client.clientSurname ?: "",
    vehicleInfo = this.vehicle?.let { "${it.make} ${it.model} (${it.regNumber})" },
    quotationDate = this.quotationDate,
    validUntil = this.validUntil,
    status = this.status,
    subtotal = this.subtotal,
    taxRate = this.taxRate,
    taxAmount = this.taxAmount,
    discountPercentage = this.discountPercentage,
    discountAmount = this.discountAmount,
    totalAmount = this.totalAmount,
    notes = this.notes,
    termsAndConditions = this.termsAndConditions,
    convertedToJobCard = this.convertedToJobCard,
    createdAt = this.createdAt,
    items = this.items.map { it.toDto() }
)

fun QuotationItem.toDto(): QuotationItemResponseDto = QuotationItemResponseDto(
    itemId = this.itemId!!,
    productCode = this.product?.productCode,
    description = this.description,
    quantity = this.quantity,
    unitPrice = this.unitPrice,
    totalPrice = this.totalPrice,
    itemType = this.itemType
)