package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.Payment
import org.prodacc.webapi.models.PaymentMethod
import org.prodacc.webapi.models.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByInvoice(invoice: Invoice): List<Payment>
    fun findByPaymentStatus(status: PaymentStatus): List<Payment>
    fun findByPaymentMethod(method: PaymentMethod): List<Payment>
    fun findByPaymentDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Payment>

    @Query("SELECT p FROM Payment p WHERE p.invoice.invoiceId = :invoiceId ORDER BY p.paymentDate DESC")
    fun findByInvoiceOrderByDateDesc(@Param("invoiceId") invoiceId: UUID): List<Payment>

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' " +
            "AND DATE(p.paymentDate) BETWEEN :startDate AND :endDate")
    fun calculatePaymentsReceived(@Param("startDate") startDate: LocalDate,
                                  @Param("endDate") endDate: LocalDate): BigDecimal?

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p " +
            "WHERE p.paymentStatus = 'COMPLETED' AND DATE(p.paymentDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.paymentMethod")
    fun getPaymentMethodBreakdown(@Param("startDate") startDate: LocalDate,
                                  @Param("endDate") endDate: LocalDate): List<Array<Any>>
}