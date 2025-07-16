package org.prodacc.webapi.services

import org.prodacc.webapi.models.Invoice

interface PdfGenerationService {
    fun generateInvoicePdf(invoice: Invoice): ByteArray
}