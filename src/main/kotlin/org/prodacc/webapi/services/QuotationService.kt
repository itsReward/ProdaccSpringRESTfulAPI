package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.prodacc.webapi.models.Quotation
import org.prodacc.webapi.models.QuotationItem
import org.prodacc.webapi.models.QuotationStatus
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.ProductRepository
import org.prodacc.webapi.repositories.QuotationItemRepository
import org.prodacc.webapi.repositories.QuotationRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.CreateQuotationDto
import org.prodacc.webapi.services.dataTransferObjects.QuotationResponseDto
import org.prodacc.webapi.services.dataTransferObjects.toDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class QuotationService(
    private val quotationRepository: QuotationRepository,
    private val quotationItemRepository: QuotationItemRepository,
    private val clientRepository: ClientRepository,
    private val vehicleRepository: VehicleRepository,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(QuotationService::class.java)

    fun getAllQuotations(): List<QuotationResponseDto> {
        logger.info("Fetching all quotations")
        return quotationRepository.findAll().map { it.toDto() }
    }

    fun getQuotationById(id: UUID): QuotationResponseDto {
        logger.info("Fetching quotation with ID: $id")
        val quotation = quotationRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Quotation not found with ID: $id") }
        return quotation.toDto()
    }

    fun createQuotation(createDto: CreateQuotationDto): QuotationResponseDto {
        logger.info("Creating new quotation for client: ${createDto.clientId}")

        val client = clientRepository.findById(createDto.clientId)
            .orElseThrow { EntityNotFoundException("Client not found with ID: ${createDto.clientId}") }

        val vehicle = createDto.vehicleId?.let {
            vehicleRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Vehicle not found with ID: $it") }
        }

        val quotationNumber = generateQuotationNumber()

        val quotation = Quotation(
            quotationNumber = quotationNumber,
            client = client,
            vehicle = vehicle,
            validUntil = createDto.validUntil,
            taxRate = createDto.taxRate,
            discountPercentage = createDto.discountPercentage,
            notes = createDto.notes,
            termsAndConditions = createDto.termsAndConditions
        )

        val savedQuotation = quotationRepository.save(quotation)

        // Create quotation items
        val items = createDto.items.map { itemDto ->
            val product = itemDto.productId?.let {
                productRepository.findById(it)
                    .orElseThrow { EntityNotFoundException("Product not found with ID: $it") }
            }

            val totalPrice = itemDto.unitPrice.multiply(itemDto.quantity)

            QuotationItem(
                quotation = savedQuotation,
                product = product,
                description = itemDto.description,
                quantity = itemDto.quantity,
                unitPrice = itemDto.unitPrice,
                totalPrice = totalPrice,
                itemType = itemDto.itemType
            )
        }

        quotationItemRepository.saveAll(items)

        // Calculate totals
        val updatedQuotation = calculateQuotationTotals(savedQuotation.copy(items = items))
        val finalQuotation = quotationRepository.save(updatedQuotation)

        logger.info("Successfully created quotation with ID: ${finalQuotation.quotationId}")
        return finalQuotation.toDto()
    }

    fun updateQuotationStatus(id: UUID, status: QuotationStatus): QuotationResponseDto {
        logger.info("Updating quotation status to $status for ID: $id")

        val quotation = quotationRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Quotation not found with ID: $id") }

        val updatedQuotation = quotation.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )

        val savedQuotation = quotationRepository.save(updatedQuotation)
        logger.info("Successfully updated quotation status for ID: $id")

        return savedQuotation.toDto()
    }

    fun convertToJobCard(quotationId: UUID, jobCardId: UUID): QuotationResponseDto {
        logger.info("Converting quotation $quotationId to job card $jobCardId")

        val quotation = quotationRepository.findById(quotationId)
            .orElseThrow { EntityNotFoundException("Quotation not found with ID: $quotationId") }

        val updatedQuotation = quotation.copy(
            convertedToJobCard = true,
            status = QuotationStatus.ACCEPTED,
            updatedAt = LocalDateTime.now()
        )

        val savedQuotation = quotationRepository.save(updatedQuotation)
        logger.info("Successfully converted quotation to job card")

        return savedQuotation.toDto()
    }

    private fun generateQuotationNumber(): String {
        val today = LocalDate.now()
        val count = quotationRepository.countTodaysQuotations()
        return "QUO${today.year}${today.monthValue.toString().padStart(2, '0')}${today.dayOfMonth.toString().padStart(2, '0')}-${(count + 1).toString().padStart(3, '0')}"
    }

    private fun calculateQuotationTotals(quotation: Quotation): Quotation {
        val subtotal = quotation.items.sumOf { it.totalPrice }
        val discountAmount = subtotal.multiply(quotation.discountPercentage).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
        val taxableAmount = subtotal.subtract(discountAmount)
        val taxAmount = taxableAmount.multiply(quotation.taxRate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
        val totalAmount = taxableAmount.add(taxAmount)

        return quotation.copy(
            subtotal = subtotal,
            discountAmount = discountAmount,
            taxAmount = taxAmount,
            totalAmount = totalAmount
        )
    }
}