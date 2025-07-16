package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.InvoiceItem
import org.prodacc.webapi.models.ItemType
import org.prodacc.webapi.models.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface InvoiceItemRepository : JpaRepository<InvoiceItem, UUID> {
    fun findByInvoice(invoice: Invoice): List<InvoiceItem>
    fun findByProduct(product: Product): List<InvoiceItem>
    fun findByItemType(itemType: ItemType): List<InvoiceItem>
    fun deleteAllByInvoice(invoice: Invoice)

    @Query("SELECT ii.product, SUM(ii.quantity) as totalSold FROM InvoiceItem ii " +
            "WHERE ii.invoice.status = 'PAID' AND ii.invoice.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ii.product ORDER BY totalSold DESC")
    fun findTopSellingProducts(@Param("startDate") startDate: LocalDate,
                               @Param("endDate") endDate: LocalDate,
                               pageable: Pageable): List<Array<Any>>
}