package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
interface InvoiceRepository : JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {

    // ===== BASIC FINDER METHODS =====

    fun findByInvoiceNumberIgnoreCase(invoiceNumber: String): Invoice?

    fun findByClient(client: Client): List<Invoice>

    fun findByJobCard(jobCard: JobCard): List<Invoice>

    fun findByQuotation(quotation: Quotation): List<Invoice>

    fun findByStatus(status: InvoiceStatus): List<Invoice>

    fun findByStatusIn(statuses: List<InvoiceStatus>): List<Invoice>

    fun findByClientAndStatusIn(client: Client, statuses: List<InvoiceStatus>): List<Invoice>

    // ===== DATE-BASED QUERIES =====

    fun findByInvoiceDate(invoiceDate: LocalDate): List<Invoice>

    fun findByInvoiceDateBetween(startDate: LocalDate, endDate: LocalDate): List<Invoice>

    fun findByDueDate(dueDate: LocalDate): List<Invoice>

    fun findByDueDateBetween(startDate: LocalDate, endDate: LocalDate): List<Invoice>

    fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Invoice>

    // ===== PAGINATED QUERIES =====

    fun findByStatus(status: InvoiceStatus, pageable: Pageable): Page<Invoice>

    fun findByStatusIn(statuses: List<InvoiceStatus>, pageable: Pageable): Page<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.client.id = :clientId ORDER BY i.invoiceDate DESC")
    fun findByClientOrderByDateDesc(@Param("clientId") clientId: UUID, pageable: Pageable): Page<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.client.id = :clientId ORDER BY i.invoiceDate DESC")
    fun findByClientOrderByDateDesc(@Param("clientId") clientId: UUID): List<Invoice>

    // ===== OVERDUE AND DUE SOON QUERIES =====

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < CURRENT_DATE AND i.status IN ('SENT', 'PARTIALLY_PAID')")
    fun findOverdueInvoices(): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :date AND i.status IN ('SENT', 'PARTIALLY_PAID')")
    fun findOverdueInvoicesBeforeDate(@Param("date") date: LocalDate): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID') " +
            "AND i.dueDate BETWEEN :startDate AND :endDate ORDER BY i.dueDate ASC")
    fun findInvoicesDueBetween(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN CURRENT_DATE AND :futureDate " +
            "AND i.status IN ('SENT', 'PARTIALLY_PAID') ORDER BY i.dueDate ASC")
    fun findInvoicesDueWithinDays(@Param("futureDate") futureDate: LocalDate): List<Invoice>

    // ===== AMOUNT-BASED QUERIES =====

    fun findByTotalAmountGreaterThan(amount: BigDecimal): List<Invoice>

    fun findByTotalAmountBetween(minAmount: BigDecimal, maxAmount: BigDecimal): List<Invoice>

    fun findByBalanceDueGreaterThan(amount: BigDecimal): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.balanceDue > 0 AND i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE')")
    fun findInvoicesWithOutstandingBalance(): List<Invoice>

    // ===== COUNTING QUERIES =====

    fun countByStatus(status: InvoiceStatus): Long

    fun countByStatusIn(statuses: List<InvoiceStatus>): Long

    fun countByClient(client: Client): Long

    fun countByInvoiceDate(invoiceDate: LocalDate): Long

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate = CURRENT_DATE")
    fun countTodaysInvoices(): Long

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    fun countInvoicesBetweenDates(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): Long

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "AND i.status = :status")
    fun countByStatusBetweenDates(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("status") status: InvoiceStatus
    ): Long

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.dueDate < CURRENT_DATE AND i.status IN ('SENT', 'PARTIALLY_PAID')")
    fun countOverdueInvoices(): Long

    // ===== FINANCIAL CALCULATION QUERIES =====

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' " +
            "AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateRevenue(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): BigDecimal?

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = :status " +
            "AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateRevenueByStatus(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("status") status: InvoiceStatus
    ): BigDecimal?

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE')")
    fun calculateTotalOutstanding(): BigDecimal?

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.dueDate < CURRENT_DATE " +
            "AND i.status IN ('SENT', 'PARTIALLY_PAID')")
    fun calculateOverdueAmount(): BigDecimal?

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.client = :client")
    fun calculateClientTotalInvoiced(@Param("client") client: Client): BigDecimal?

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.client = :client " +
            "AND i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE')")
    fun calculateClientOutstandingBalance(@Param("client") client: Client): BigDecimal?

    @Query("SELECT AVG(i.totalAmount) FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateAverageInvoiceValue(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): BigDecimal?

    // ===== MONTHLY/YEARLY AGGREGATION QUERIES =====

    @Query("SELECT YEAR(i.invoiceDate) as year, MONTH(i.invoiceDate) as month, SUM(i.totalAmount) as revenue " +
            "FROM Invoice i WHERE i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(i.invoiceDate), MONTH(i.invoiceDate) ORDER BY YEAR(i.invoiceDate), MONTH(i.invoiceDate)")
    fun calculateMonthlyRevenue(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    @Query("SELECT YEAR(i.invoiceDate) as year, SUM(i.totalAmount) as revenue " +
            "FROM Invoice i WHERE i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(i.invoiceDate) ORDER BY YEAR(i.invoiceDate)")
    fun calculateYearlyRevenue(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // ===== TOP CLIENTS AND ANALYTICS =====

    @Query("SELECT i.client, SUM(i.totalAmount) as revenue FROM Invoice i " +
            "WHERE i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.client ORDER BY revenue DESC")
    fun findTopClientsByRevenue(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): List<Array<Any>>

    @Query("SELECT i.client, COUNT(i) as invoiceCount FROM Invoice i " +
            "WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.client ORDER BY invoiceCount DESC")
    fun findTopClientsByInvoiceCount(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): List<Array<Any>>

    @Query("SELECT i.status, COUNT(i) as count FROM Invoice i " +
            "WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.status")
    fun getStatusBreakdown(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // ===== AGING ANALYSIS =====

    @Query("SELECT " +
            "SUM(CASE WHEN i.dueDate IS NULL OR i.dueDate >= CURRENT_DATE THEN i.balanceDue ELSE 0 END) as current, " +
            "SUM(CASE WHEN i.dueDate < CURRENT_DATE AND TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) BETWEEN 1 AND 30 THEN i.balanceDue ELSE 0 END) as days1to30, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) BETWEEN 31 AND 60 THEN i.balanceDue ELSE 0 END) as days31to60, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) BETWEEN 61 AND 90 THEN i.balanceDue ELSE 0 END) as days61to90, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) > 90 THEN i.balanceDue ELSE 0 END) as days90plus " +
            "FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE')")
    fun getAgingAnalysis(): List<Array<Any>>

    @Query("SELECT i.client, " +
            "SUM(CASE WHEN i.dueDate IS NULL OR i.dueDate >= CURRENT_DATE THEN i.balanceDue ELSE 0 END) as current, " +
            "SUM(CASE WHEN i.dueDate < CURRENT_DATE AND DATEDIFF(CURRENT_DATE, i.dueDate) BETWEEN 1 AND 30 THEN i.balanceDue ELSE 0 END) as days1to30, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) BETWEEN 31 AND 60 THEN i.balanceDue ELSE 0 END) as days31to60, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) BETWEEN 61 AND 90 THEN i.balanceDue ELSE 0 END) as days61to90, " +
            "SUM(CASE WHEN TIMESTAMPDIFF(DAY, i.dueDate, CURRENT_DATE) > 90 THEN i.balanceDue ELSE 0 END) as days90plus " +
            "FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE') " +
            "GROUP BY i.client HAVING (current + days1to30 + days31to60 + days61to90 + days90plus) > 0 " +
            "ORDER BY (current + days1to30 + days31to60 + days61to90 + days90plus) DESC")
    fun getClientAgingAnalysis(pageable: Pageable): List<Array<Any>>

    // ===== SEARCH AND FILTERING =====

    @Query("SELECT i FROM Invoice i WHERE " +
            "(:invoiceNumber IS NULL OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :invoiceNumber, '%'))) AND " +
            "(:clientName IS NULL OR LOWER(i.client.clientName) LIKE LOWER(CONCAT('%', :clientName, '%')) OR LOWER(i.client.clientSurname) LIKE LOWER(CONCAT('%', :clientName, '%'))) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:minAmount IS NULL OR i.totalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR i.totalAmount <= :maxAmount) AND " +
            "(:fromDate IS NULL OR i.invoiceDate >= :fromDate) AND " +
            "(:toDate IS NULL OR i.invoiceDate <= :toDate)")
    fun searchInvoices(
        @Param("invoiceNumber") invoiceNumber: String?,
        @Param("clientName") clientName: String?,
        @Param("status") status: InvoiceStatus?,
        @Param("minAmount") minAmount: BigDecimal?,
        @Param("maxAmount") maxAmount: BigDecimal?,
        @Param("fromDate") fromDate: LocalDate?,
        @Param("toDate") toDate: LocalDate?,
        pageable: Pageable
    ): Page<Invoice>

    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN i.items item WHERE " +
            "(:searchTerm IS NULL OR " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.client.clientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.client.clientSurname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(item.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    fun fullTextSearch(
        @Param("searchTerm") searchTerm: String?,
        pageable: Pageable
    ): Page<Invoice>

    // ===== DASHBOARD AND METRICS =====

    @Query("SELECT " +
            "COUNT(CASE WHEN i.status = 'DRAFT' THEN 1 END) as draft, " +
            "COUNT(CASE WHEN i.status = 'SENT' THEN 1 END) as sent, " +
            "COUNT(CASE WHEN i.status = 'PAID' THEN 1 END) as paid, " +
            "COUNT(CASE WHEN i.status = 'PARTIALLY_PAID' THEN 1 END) as partiallyPaid, " +
            "COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) as overdue, " +
            "COUNT(CASE WHEN i.status = 'CANCELLED' THEN 1 END) as cancelled " +
            "FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    fun getDashboardMetrics(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    @Query("SELECT " +
            "SUM(i.totalAmount) as totalInvoiced, " +
            "SUM(i.amountPaid) as totalPaid, " +
            "SUM(i.balanceDue) as totalOutstanding, " +
            "AVG(i.totalAmount) as averageInvoiceValue " +
            "FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    fun getFinancialSummary(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // ===== PERFORMANCE AND EFFICIENCY QUERIES =====

    @Query("SELECT AVG(DATEDIFF(p.paymentDate, i.invoiceDate)) FROM Invoice i " +
            "JOIN i.payments p WHERE i.status = 'PAID' " +
            "AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateAveragePaymentDays(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): Double?

    @Query("SELECT " +
            "(COUNT(CASE WHEN i.status = 'PAID' THEN 1 END) * 100.0 / COUNT(i)) as paymentRate " +
            "FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "AND i.status != 'DRAFT'")
    fun calculatePaymentSuccessRate(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): Double?

    // ===== BULK OPERATIONS =====

    @Modifying
    @Query("UPDATE Invoice i SET i.status = :status, i.updatedAt = CURRENT_TIMESTAMP WHERE i.invoiceId IN :invoiceIds")
    fun bulkUpdateStatus(
        @Param("invoiceIds") invoiceIds: List<UUID>,
        @Param("status") status: InvoiceStatus
    ): Int

    /*@Modifying
    @Query("UPDATE Invoice i SET i.reminderSent = true, i.updatedAt = CURRENT_TIMESTAMP WHERE i.invoiceId IN :invoiceIds")
    fun markRemindersAsSent(@Param("invoiceIds") invoiceIds: List<UUID>): Int*/

    // ===== VALIDATION QUERIES =====

    fun existsByInvoiceNumberIgnoreCase(invoiceNumber: String): Boolean

    @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.client = :client AND i.status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE')")
    fun hasOutstandingInvoices(@Param("client") client: Client): Boolean

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.jobCard = :jobCard")
    fun countInvoicesForJobCard(@Param("jobCard") jobCard: JobCard): Long

    // ===== AUDIT AND TRACKING =====

    @Query("SELECT i FROM Invoice i WHERE i.updatedAt > :since ORDER BY i.updatedAt DESC")
    fun findRecentlyModified(@Param("since") since: LocalDateTime): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    fun findCreatedBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Invoice>

    // ===== NOTIFICATION QUERIES =====

    @Query("SELECT i FROM Invoice i WHERE i.dueDate = :date AND i.status IN ('SENT', 'PARTIALLY_PAID') ")
    fun findInvoicesNeedingReminders(@Param("date") date: LocalDate): List<Invoice>

    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN CURRENT_DATE AND :futureDate " +
            "AND i.status IN ('SENT', 'PARTIALLY_PAID')")
    fun findUpcomingDueInvoices(@Param("futureDate") futureDate: LocalDate): List<Invoice>

    // ===== SPECIALIZED BUSINESS QUERIES =====

    @Query("SELECT i FROM Invoice i WHERE i.client = :client " +
            "AND i.status = 'PAID' ORDER BY i.invoiceDate DESC")
    fun findClientPaidInvoices(@Param("client") client: Client, pageable: Pageable): Page<Invoice>

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.jobCard.supervisor = :supervisor " +
            "AND i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateSupervisorRevenue(
        @Param("supervisor") supervisor: Employee,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): BigDecimal?

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.jobCard.serviceAdvisor = :serviceAdvisor " +
            "AND i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateServiceAdvisorRevenue(
        @Param("serviceAdvisor") serviceAdvisor: Employee,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): BigDecimal?

    // ===== COMPLIANCE AND REPORTING =====

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "AND i.taxAmount > 0 ORDER BY i.invoiceDate")
    fun findTaxableInvoices(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Invoice>

    @Query("SELECT SUM(i.taxAmount) FROM Invoice i WHERE i.status = 'PAID' " +
            "AND i.invoiceDate BETWEEN :startDate AND :endDate")
    fun calculateTaxCollected(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): BigDecimal?

    @Query("SELECT i.client, COUNT(i) as invoiceCount, SUM(i.totalAmount) as totalAmount " +
            "FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.client HAVING SUM(i.totalAmount) > :threshold " +
            "ORDER BY totalAmount DESC")
    fun findHighValueClients(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("threshold") threshold: BigDecimal,
        pageable: Pageable
    ): List<Array<Any>>
}