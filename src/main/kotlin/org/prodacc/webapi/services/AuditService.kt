package org.prodacc.webapi.services

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.InvoiceStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.java

@Service
class AuditService {
    private val logger = LoggerFactory.getLogger(AuditService::class.java)

    fun logInvoiceCreated(invoice: Invoice) {
        logger.info("AUDIT: Invoice created - ID: ${invoice.invoiceId}, Number: ${invoice.invoiceNumber}")
    }

    fun logInvoiceUpdated(invoice: Invoice) {
        logger.info("AUDIT: Invoice updated - ID: ${invoice.invoiceId}, Number: ${invoice.invoiceNumber}")
    }

    fun logInvoiceDeleted(invoice: Invoice) {
        logger.info("AUDIT: Invoice deleted - ID: ${invoice.invoiceId}, Number: ${invoice.invoiceNumber}")
    }

    fun logInvoiceStatusChanged(invoice: Invoice, oldStatus: InvoiceStatus, newStatus: InvoiceStatus) {
        logger.info("AUDIT: Invoice status changed - ID: ${invoice.invoiceId}, ${oldStatus} -> ${newStatus}")
    }

    fun logInvoiceSent(invoice: Invoice) {
        logger.info("AUDIT: Invoice sent - ID: ${invoice.invoiceId}, Number: ${invoice.invoiceNumber}")
    }

    fun logInvoiceCancelled(invoice: Invoice, reason: String?) {
        logger.info("AUDIT: Invoice cancelled - ID: ${invoice.invoiceId}, Reason: ${reason ?: "No reason provided"}")
    }

    fun logInvoiceEmailed(invoice: Invoice, emailAddress: String) {
        logger.info("AUDIT: Invoice emailed - ID: ${invoice.invoiceId}, To: $emailAddress")
    }
}