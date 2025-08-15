package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface JobCardPartsRequisitionRepository : JpaRepository<JobCardPartsRequisition, UUID> {

    // ===== BASIC QUERIES =====

    fun findByJobCardOrderByRequestedAtAsc(jobCard: JobCard): List<JobCardPartsRequisition>

    fun findByJobCardAndStatusIn(jobCard: JobCard, statuses: List<PartRequisitionStatus>): List<JobCardPartsRequisition>

    fun findByStatusOrderByRequestedAtAsc(status: PartRequisitionStatus): List<JobCardPartsRequisition>

    fun findByRequestedByOrderByRequestedAtDesc(requestedBy: Employee): List<JobCardPartsRequisition>

    fun findByJobCardAndProductAndStatus(jobCard: JobCard, product: Product, status: PartRequisitionStatus): Optional<JobCardPartsRequisition>

    // ===== STORES MANAGEMENT QUERIES =====

    @Query("""
        SELECT r FROM JobCardPartsRequisition r 
        WHERE r.status = 'REQUESTED' 
        ORDER BY r.requestedAt ASC
    """)
    fun findPendingRequisitions(): List<JobCardPartsRequisition>

    @Query("""
        SELECT r FROM JobCardPartsRequisition r 
        WHERE r.status IN ('REQUESTED', 'DISBURSED') 
        AND r.product.productId = :productId
        ORDER BY r.requestedAt ASC
    """)
    fun findActiveRequisitionsByProduct(@Param("productId") productId: UUID): List<JobCardPartsRequisition>

    // ===== INVOICING QUERIES =====

    @Query("""
        SELECT r FROM JobCardPartsRequisition r 
        WHERE r.jobCard.jobId = :jobCardId 
        AND r.status IN ('USED', 'PARTIALLY_USED')
        AND r.usedQuantity > 0
        ORDER BY r.usedAt ASC
    """)
    fun findUsedPartsForInvoicing(@Param("jobCardId") jobCardId: UUID): List<JobCardPartsRequisition>

    // ===== ANALYTICS & REPORTING QUERIES =====

    @Query("""
        SELECT 
            COUNT(*) as totalRequested,
            SUM(CASE WHEN r.status = 'DISBURSED' THEN 1 ELSE 0 END) as totalDisbursed,
            SUM(CASE WHEN r.status IN ('USED', 'PARTIALLY_USED') THEN 1 ELSE 0 END) as totalUsed,
            SUM(CASE WHEN r.status = 'NOT_AVAILABLE' THEN 1 ELSE 0 END) as totalNotAvailable
        FROM JobCardPartsRequisition r 
        WHERE r.requestedAt BETWEEN :startDate AND :endDate
    """)
    fun getBasicStatistics(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): RequisitionStats

    @Query("""
        SELECT 
            r.product.productId as productId,
            r.product.productName as productName,
            SUM(r.usedQuantity) as totalQuantityUsed,
            SUM(r.totalCost) as totalCost,
            COUNT(r) as usageCount
        FROM JobCardPartsRequisition r 
        WHERE r.status IN ('USED', 'PARTIALLY_USED')
        AND r.usedAt BETWEEN :startDate AND :endDate
        GROUP BY r.product.productId, r.product.productName
        ORDER BY SUM(r.usedQuantity) DESC
    """)
    fun getMostUsedProducts(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): List<ProductUsageStats>

    @Query("""
        SELECT AVG(
            CASE 
                WHEN r.usedAt IS NOT NULL THEN 
                    EXTRACT(TIMESTAMP FROM (r.usedAt - r.requestedAt)) / 3600.0
                WHEN r.status = 'NOT_AVAILABLE' AND r.approvedAt IS NOT NULL THEN 
                    EXTRACT(TIMESTAMP FROM (r.approvedAt - r.requestedAt)) / 3600.0
                ELSE NULL 
            END
        )
        FROM JobCardPartsRequisition r 
        WHERE r.requestedAt BETWEEN :startDate AND :endDate
        AND r.status IN ('USED', 'PARTIALLY_USED', 'NOT_AVAILABLE')
    """, nativeQuery = true)
    fun getAverageProcessingTimeInHours(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): Double?

    // ===== INVENTORY IMPACT QUERIES =====

    @Query("""
        SELECT SUM(r.usedQuantity) 
        FROM JobCardPartsRequisition r 
        WHERE r.product.productId = :productId 
        AND r.status IN ('USED', 'PARTIALLY_USED')
        AND r.usedAt BETWEEN :startDate AND :endDate
    """)
    fun getTotalUsageByProduct(@Param("productId") productId: UUID, @Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): BigDecimal?

    @Query("""
        SELECT r FROM JobCardPartsRequisition r 
        WHERE r.status = 'DISBURSED' 
        AND r.disbursedAt < :cutoffTime
        ORDER BY r.disbursedAt ASC
    """)
    fun findUnusedDisbursedParts(@Param("cutoffTime") cutoffTime: LocalDateTime): List<JobCardPartsRequisition>

    // ===== EMPLOYEE PERFORMANCE QUERIES =====

    @Query("""
        SELECT 
            r.requestedBy.employeeId as employeeId,
            r.requestedBy.employeeName as employeeName,
            COUNT(r) as totalRequests,
            SUM(CASE WHEN r.status IN ('USED', 'PARTIALLY_USED') THEN 1 ELSE 0 END) as completedRequests
        FROM JobCardPartsRequisition r 
        WHERE r.requestedAt BETWEEN :startDate AND :endDate
        GROUP BY r.requestedBy.employeeId, r.requestedBy.employeeName
        ORDER BY COUNT(r) DESC
    """)
    fun getEmployeeRequisitionStats(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): List<EmployeeRequisitionStats>

   /* @Query(
        ("SELECT new RequisitionStats(" +
                "COUNT(r), SUM(r.totalAmount), AVG(r.totalAmount)) " +
                "FROM JobCardPartsRequisition r " +
                "WHERE r.createdAt BETWEEN :startDate AND :endDate")
    )
    fun getStatisticsByDateRange(
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?
    ): RequisitionStats?*/


}

// ===== PROJECTION INTERFACES =====

interface RequisitionStats {
    val totalRequested: Long
    val totalDisbursed: Long
    val totalUsed: Long
    val totalNotAvailable: Long
}

interface ProductUsageStats {
    val productId: UUID
    val productName: String
    val totalQuantityUsed: BigDecimal
    val totalCost: BigDecimal
    val usageCount: Long
}

interface EmployeeRequisitionStats {
    val employeeId: UUID
    val employeeName: String
    val totalRequests: Long
    val completedRequests: Long
}