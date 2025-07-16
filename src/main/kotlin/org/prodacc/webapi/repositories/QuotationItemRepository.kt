package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.ItemType
import org.prodacc.webapi.models.Product
import org.prodacc.webapi.models.Quotation
import org.prodacc.webapi.models.QuotationItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuotationItemRepository : JpaRepository<QuotationItem, UUID> {
    fun findByQuotation(quotation: Quotation): List<QuotationItem>
    fun findByProduct(product: Product): List<QuotationItem>
    fun findByItemType(itemType: ItemType): List<QuotationItem>
}
