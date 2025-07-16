package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.Quotation
import org.prodacc.webapi.models.QuotationStatus
import org.prodacc.webapi.models.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Repository
interface QuotationRepository : JpaRepository<Quotation, UUID> {
    fun findByClient(client: Client): List<Quotation>
    fun findByVehicle(vehicle: Vehicle): List<Quotation>
    fun findByStatus(status: QuotationStatus): List<Quotation>
    fun findByQuotationDateBetween(startDate: LocalDate, endDate: LocalDate): List<Quotation>
    fun findByQuotationNumberIgnoreCase(quotationNumber: String): Quotation?

    @Query("SELECT q FROM Quotation q WHERE q.validUntil < CURRENT_DATE AND q.status = 'SENT'")
    fun findExpiredQuotations(): List<Quotation>

    @Query("SELECT q FROM Quotation q WHERE q.client.id = :clientId ORDER BY q.quotationDate DESC")
    fun findByClientOrderByDateDesc(@Param("clientId") clientId: UUID): List<Quotation>

    @Query("SELECT SUM(q.totalAmount) FROM Quotation q WHERE q.status = 'ACCEPTED' " +
            "AND q.quotationDate BETWEEN :startDate AND :endDate")
    fun calculateAcceptedQuotationsValue(@Param("startDate") startDate: LocalDate,
                                         @Param("endDate") endDate: LocalDate): BigDecimal?

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.quotationDate = CURRENT_DATE")
    fun countTodaysQuotations(): Long

    fun existsByQuotationNumberIgnoreCase(quotationNumber: String): Boolean
}