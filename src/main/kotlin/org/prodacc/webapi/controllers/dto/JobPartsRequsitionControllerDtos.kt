package org.prodacc.webapi.controllers.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.UUID

// Enhanced DTOs with validation
data class CreatePartRequisitionDto(
    @field:NotNull(message = "Job card ID is required")
    val jobCardId: UUID,

    @field:NotNull(message = "Product ID is required")
    val productId: UUID,

    @field:NotNull(message = "Requested quantity is required")
    @field:DecimalMin(value = "0.01", message = "Requested quantity must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid quantity format")
    val requestedQuantity: BigDecimal,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)

data class ApprovePartRequisitionDto(
    @field:NotNull(message = "Approved quantity is required")
    @field:DecimalMin(value = "0.01", message = "Approved quantity must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid quantity format")
    val approvedQuantity: BigDecimal,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)

data class DisbursePartRequisitionDto(
    @field:NotNull(message = "Disbursed quantity is required")
    @field:DecimalMin(value = "0.01", message = "Disbursed quantity must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid quantity format")
    val disbursedQuantity: BigDecimal,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)

data class MarkAsUsedDto(
    @field:NotNull(message = "Used quantity is required")
    @field:DecimalMin(value = "0.01", message = "Used quantity must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid quantity format")
    val usedQuantity: BigDecimal,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)

data class MarkAsNotAvailableDto(
    @field:NotBlank(message = "Reason is required")
    @field:Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    val reason: String,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)