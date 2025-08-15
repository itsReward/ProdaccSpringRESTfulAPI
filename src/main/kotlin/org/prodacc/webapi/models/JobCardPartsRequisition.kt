package org.prodacc.webapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "job_card_parts_requisition")
data class JobCardPartsRequisition(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "requisition_id", nullable = false)
    var requisitionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "job_card_id", nullable = false)
    var jobCard: JobCard,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    var requestedBy: Employee,

    @Column(name = "requested_quantity", nullable = false, precision = 10, scale = 2)
    var requestedQuantity: BigDecimal,

    @Column(name = "approved_quantity", precision = 10, scale = 2)
    var approvedQuantity: BigDecimal = BigDecimal.ZERO,

    @Column(name = "disbursed_quantity", precision = 10, scale = 2)
    var disbursedQuantity: BigDecimal = BigDecimal.ZERO,

    @Column(name = "used_quantity", precision = 10, scale = 2)
    var usedQuantity: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    var status: PartRequisitionStatus = PartRequisitionStatus.REQUESTED,

    @Column(name = "requested_at", nullable = false)
    var requestedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "approved_at")
    var approvedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: Employee? = null,

    @Column(name = "disbursed_at")
    var disbursedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disbursed_by")
    var disbursedBy: Employee? = null,

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_used_by")
    var markedUsedBy: Employee? = null,

    @Column(name = "unit_cost", precision = 12, scale = 2)
    var unitCost: BigDecimal? = null,

    @Column(name = "total_cost", precision = 12, scale = 2, insertable = false, updatable = false)
    var totalCost: BigDecimal? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Version
    @Column(name = "version")
    var version: Long = 0
) {

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    // Helper methods for status transitions
    fun canApprove(): Boolean = status == PartRequisitionStatus.REQUESTED

    fun canDisburse(): Boolean = status == PartRequisitionStatus.REQUESTED && approvedQuantity > BigDecimal.ZERO

    fun canMarkAsUsed(): Boolean = status == PartRequisitionStatus.DISBURSED

    fun canMarkAsNotAvailable(): Boolean = status == PartRequisitionStatus.REQUESTED

    // Business logic methods
    fun approve(approver: Employee, approvedQty: BigDecimal) {
        require(canApprove()) { "Cannot approve requisition in status: $status" }
        require(approvedQty <= requestedQuantity) { "Approved quantity cannot exceed requested quantity" }

        this.approvedQuantity = approvedQty
        this.approvedBy = approver
        this.approvedAt = LocalDateTime.now()
        this.unitCost = product.sellingPrice // Set cost at approval time
    }

    fun disburse(disbursedBy: Employee, disbursedQty: BigDecimal) {
        require(canDisburse()) { "Cannot disburse requisition in status: $status" }
        require(disbursedQty <= approvedQuantity) { "Disbursed quantity cannot exceed approved quantity" }

        this.disbursedQuantity = disbursedQty
        this.disbursedBy = disbursedBy
        this.disbursedAt = LocalDateTime.now()
        this.status = PartRequisitionStatus.DISBURSED
    }

    fun markAsUsed(markedBy: Employee, usedQty: BigDecimal) {
        require(canMarkAsUsed()) { "Cannot mark as used in status: $status" }
        require(usedQty <= disbursedQuantity) { "Used quantity cannot exceed disbursed quantity" }

        this.usedQuantity = usedQty
        this.markedUsedBy = markedBy
        this.usedAt = LocalDateTime.now()
        this.status = if (usedQty == disbursedQuantity) PartRequisitionStatus.USED else PartRequisitionStatus.PARTIALLY_USED
    }

    fun markAsNotAvailable(rejectedBy: Employee, reason: String) {
        require(canMarkAsNotAvailable()) { "Cannot mark as not available in status: $status" }

        this.approvedBy = rejectedBy
        this.approvedAt = LocalDateTime.now()
        this.rejectionReason = reason
        this.status = PartRequisitionStatus.NOT_AVAILABLE
    }
}

enum class PartRequisitionStatus {
    REQUESTED,      // Initial state when technician requests parts
    DISBURSED,      // Stores has physically given parts to technician
    NOT_AVAILABLE,  // Stores marked as not available
    USED,           // Technician marked as completely used
    PARTIALLY_USED, // Technician marked as partially used
    CANCELLED       // Requisition was cancelled
}
