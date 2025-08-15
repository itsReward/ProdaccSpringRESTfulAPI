package org.prodacc.webapi.services.dataTransferObjects

import org.prodacc.webapi.models.JobCardPartsRequisition
import org.prodacc.webapi.models.PartRequisitionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID


// Response DTOs
data class PartRequisitionResponseDto(
    val requisitionId: UUID,
    val jobCardId: UUID,
    val jobCardNumber: Int,
    val productId: UUID,
    val productName: String,
    val productCode: String,
    val requestedBy: EmployeeSummaryDto,
    val requestedQuantity: BigDecimal,
    val approvedQuantity: BigDecimal,
    val disbursedQuantity: BigDecimal,
    val usedQuantity: BigDecimal,
    val status: PartRequisitionStatus,
    val requestedAt: LocalDateTime,
    val approvedAt: LocalDateTime?,
    val approvedBy: EmployeeSummaryDto?,
    val disbursedAt: LocalDateTime?,
    val disbursedBy: EmployeeSummaryDto?,
    val usedAt: LocalDateTime?,
    val markedUsedBy: EmployeeSummaryDto?,
    val unitCost: BigDecimal?,
    val totalCost: BigDecimal?,
    val notes: String?,
    val rejectionReason: String?
)

data class EmployeeSummaryDto(
    val employeeId: UUID,
    val name: String,
    val role: String
)

// Request DTOs
data class CreatePartRequisitionDto(
    val jobCardId: UUID,
    val productId: UUID,
    val requestedQuantity: BigDecimal,
    val notes: String? = null
)

data class ApprovePartRequisitionDto(
    val approvedQuantity: BigDecimal,
    val notes: String? = null
)

data class DisbursePartRequisitionDto(
    val disbursedQuantity: BigDecimal,
    val notes: String? = null
)

data class MarkAsUsedDto(
    val usedQuantity: BigDecimal,
    val notes: String? = null
)

data class MarkAsNotAvailableDto(
    val reason: String,
    val notes: String? = null
)

// Extension functions for easy conversion
fun JobCardPartsRequisition.toResponseDto(): PartRequisitionResponseDto {
    return PartRequisitionResponseDto(
        requisitionId = this.requisitionId!!,
        jobCardId = this.jobCard.jobId!!,
        jobCardNumber = this.jobCard.jobCardNumber!!,
        productId = this.product.productId!!,
        productName = this.product.productName!!,
        productCode = this.product.productCode!!,
        requestedBy = EmployeeSummaryDto(
            employeeId = this.requestedBy.employeeId!!,
            name = "${this.requestedBy.employeeName} ${this.requestedBy.employeeSurname}",
            role = this.requestedBy.employeeRole!!
        ),
        requestedQuantity = this.requestedQuantity,
        approvedQuantity = this.approvedQuantity,
        disbursedQuantity = this.disbursedQuantity,
        usedQuantity = this.usedQuantity,
        status = this.status,
        requestedAt = this.requestedAt,
        approvedAt = this.approvedAt,
        approvedBy = this.approvedBy?.let {
            EmployeeSummaryDto(
                employeeId = it.employeeId!!,
                name = "${it.employeeName} ${it.employeeSurname}",
                role = it.employeeRole!!
            )
        },
        disbursedAt = this.disbursedAt,
        disbursedBy = this.disbursedBy?.let {
            EmployeeSummaryDto(
                employeeId = it.employeeId!!,
                name = "${it.employeeName} ${it.employeeSurname}",
                role = it.employeeRole!!
            )
        },
        usedAt = this.usedAt,
        markedUsedBy = this.markedUsedBy?.let {
            EmployeeSummaryDto(
                employeeId = it.employeeId!!,
                name = "${it.employeeName} ${it.employeeSurname}",
                role = it.employeeRole!!
            )
        },
        unitCost = this.unitCost,
        totalCost = this.totalCost,
        notes = this.notes,
        rejectionReason = this.rejectionReason
    )
}

// Statistics DTO
data class RequisitionStatisticsDto(
    val totalRequested: Long,
    val totalDisbursed: Long,
    val totalUsed: Long,
    val totalNotAvailable: Long,
    val averageProcessingTime: Double, // in hours
    val mostRequestedProducts: List<ProductUsageDto>
)

data class ProductUsageDto(
    val productId: UUID,
    val productName: String,
    val totalQuantityUsed: BigDecimal,
    val totalCost: BigDecimal,
    val usageCount: Long
)