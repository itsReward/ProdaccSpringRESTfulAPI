package org.prodacc.webapi.services

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.services.dataTransferObjects.EmailInvoiceRequest
import org.prodacc.webapi.services.dataTransferObjects.EmailResult

interface EmailService {
    fun sendInvoiceEmail(invoice: Invoice, emailRequest: EmailInvoiceRequest, pdfBytes: ByteArray?): EmailResult
}
