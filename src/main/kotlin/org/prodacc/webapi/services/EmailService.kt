package org.prodacc.webapi.services

import org.prodacc.webapi.models.Invoice
import org.prodacc.webapi.services.dataTransferObjects.EmailInvoiceRequest
import org.prodacc.webapi.services.dataTransferObjects.EmailResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.java

@Service
class EmailService {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendInvoiceEmail(invoice: Invoice, emailRequest: EmailInvoiceRequest, pdfBytes: ByteArray?): EmailResult {
        logger.info("Sending invoice email to ${emailRequest.emailAddress} for invoice ${invoice.invoiceNumber}")

        // TODO: Implement actual email sending logic
        // For now, just log and return success
        logger.info("Email would be sent to: ${emailRequest.emailAddress}")
        logger.info("Subject: ${emailRequest.subject}")
        logger.info("PDF attached: ${pdfBytes != null}")

        return EmailResult(
            success = true,
            message = "Email sent successfully (simulated)"
        )
    }
}