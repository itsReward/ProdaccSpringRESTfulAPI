package org.prodacc.webapi.services

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.models.InvoiceStatus

interface AuditService {
    fun logInvoiceCreated(invoice: Invoice)
    fun logInvoiceUpdated(invoice: Invoice)
    fun logInvoiceDeleted(invoice: Invoice)
    fun logInvoiceStatusChanged(invoice: Invoice, oldStatus: InvoiceStatus, newStatus: InvoiceStatus)
    fun logInvoiceSent(invoice: Invoice)
    fun logInvoiceCancelled(invoice: Invoice, reason: String?)
    fun logInvoiceEmailed(invoice: Invoice, emailAddress: String)
}