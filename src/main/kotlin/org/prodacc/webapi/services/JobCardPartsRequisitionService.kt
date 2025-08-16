package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.*
import org.prodacc.webapi.repositories.*
import org.prodacc.webapi.services.dataTransferObjects.ApprovePartRequisitionDto
import org.prodacc.webapi.services.dataTransferObjects.CreatePartRequisitionDto
import org.prodacc.webapi.services.dataTransferObjects.DisbursePartRequisitionDto
import org.prodacc.webapi.services.dataTransferObjects.MarkAsNotAvailableDto
import org.prodacc.webapi.services.dataTransferObjects.MarkAsUsedDto
import org.prodacc.webapi.services.dataTransferObjects.PartRequisitionResponseDto
import org.prodacc.webapi.services.dataTransferObjects.RequisitionStatisticsDto
import org.prodacc.webapi.services.dataTransferObjects.toResponseDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class JobCardPartsRequisitionService(
    private val partsRequisitionRepository: JobCardPartsRequisitionRepository,
    private val jobCardRepository: JobCardRepository,
    private val productRepository: ProductRepository,
    private val employeeRepository: EmployeeRepository,
    private val inventoryTransactionRepository: InventoryTransactionRepository,
    private val webSocketHandler: org.prodacc.webapi.services.synchronisation.WebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllRequisitions(): List<PartRequisitionResponseDto> {
        logger.info("Fetching all parts requisitions")
        return partsRequisitionRepository.findAll()
            .map { it.toResponseDto() }
    }


    fun getRequisitionById(requisitionId: UUID): PartRequisitionResponseDto {
        logger.info("Fetching requisition by ID: $requisitionId")
        return partsRequisitionRepository.findById(requisitionId)
            .orElseThrow { EntityNotFoundException("Requisition not found: $requisitionId") }
            .toResponseDto()
    }


    // ===== TECHNICIAN OPERATIONS =====

    fun requestParts(requestDto: CreatePartRequisitionDto, requesterId: UUID): PartRequisitionResponseDto {
        logger.info("Creating parts requisition for job card: ${requestDto.jobCardId}")

        val jobCard = jobCardRepository.findById(requestDto.jobCardId)
            .orElseThrow { EntityNotFoundException("Job card not found: ${requestDto.jobCardId}") }

        val product = productRepository.findById(requestDto.productId)
            .orElseThrow { EntityNotFoundException("Product not found: ${requestDto.productId}") }

        val requester = employeeRepository.findById(requesterId)
            .orElseThrow { EntityNotFoundException("Employee not found: $requesterId") }

        // Validate that requester is assigned to this job card
        validateTechnicianAssignment(jobCard, requester)

        // Check if there's already a pending request for this product
        val existingRequisition = partsRequisitionRepository
            .findByJobCardAndProductAndStatus(jobCard, product, PartRequisitionStatus.REQUESTED)

        if (existingRequisition.isPresent) {
            throw IllegalStateException("There's already a pending requisition for this product")
        }

        val requisition = JobCardPartsRequisition(
            jobCard = jobCard,
            product = product,
            requestedBy = requester,
            requestedQuantity = requestDto.requestedQuantity,
            notes = requestDto.notes,
            unitCost = product.sellingPrice
        )

        val savedRequisition = partsRequisitionRepository.save(requisition)

        // Notify stores manager
       // webSocketHandler.broadcastToRole("STORES", "PARTS_REQUESTED", savedRequisition.requisitionId)

        logger.info("Parts requisition created with ID: ${savedRequisition.requisitionId}")
        return savedRequisition.toResponseDto()
    }

    fun markPartsAsUsed(requisitionId: UUID, markAsUsedDto: MarkAsUsedDto, employeeId: UUID): PartRequisitionResponseDto {
        logger.info("Marking parts as used for requisition: $requisitionId")

        val requisition = partsRequisitionRepository.findById(requisitionId)
            .orElseThrow { EntityNotFoundException("Requisition not found: $requisitionId") }

        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { EntityNotFoundException("Employee not found: $employeeId") }

        // Validate permissions
        validateTechnicianAssignment(requisition.jobCard, employee)

        if (!requisition.canMarkAsUsed()) {
            throw IllegalStateException("Cannot mark parts as used. Current status: ${requisition.status}")
        }

        requisition.markAsUsed(employee, markAsUsedDto.usedQuantity)

        if (markAsUsedDto.notes != null) {
            requisition.notes = if (requisition.notes.isNullOrBlank()) {
                markAsUsedDto.notes
            } else {
                "${requisition.notes}\nUsed: ${markAsUsedDto.notes}"
            }
        }

        val savedRequisition = partsRequisitionRepository.save(requisition)

        // Notify relevant parties
        //webSocketHandler.broadcastToRole("STORES", "PARTS_USED", savedRequisition.requisitionId)
        //webSocketHandler.broadcastToRole("ADMIN", "PARTS_USED", savedRequisition.requisitionId)

        logger.info("Parts marked as used for requisition: $requisitionId")
        return savedRequisition.toResponseDto()
    }

    // ===== STORES MANAGER OPERATIONS =====

    fun getPendingRequisitions(): List<PartRequisitionResponseDto> {
        logger.info("Fetching pending parts requisitions")
        return partsRequisitionRepository.findByStatusOrderByRequestedAtAsc(PartRequisitionStatus.REQUESTED)
            .map { it.toResponseDto() }
    }

    fun approveAndDisburseRequisition(
        requisitionId: UUID,
        approveDto: ApprovePartRequisitionDto,
        disburseDto: DisbursePartRequisitionDto,
        storesManagerId: UUID
    ): PartRequisitionResponseDto {
        logger.info("Approving and disbursing requisition: $requisitionId")

        val requisition = partsRequisitionRepository.findById(requisitionId)
            .orElseThrow { EntityNotFoundException("Requisition not found: $requisitionId") }

        val storesManager = employeeRepository.findById(storesManagerId)
            .orElseThrow { EntityNotFoundException("Employee not found: $storesManagerId") }

        // Validate permissions
        if (!hasStoresPermission(storesManager)) {
            throw IllegalStateException("Employee does not have stores management permissions")
        }

        // Check product availability
        if (requisition.product.currentStock.toBigDecimal() < disburseDto.disbursedQuantity) {
            throw IllegalStateException("Insufficient stock. Available: ${requisition.product.currentStock}, Requested: ${disburseDto.disbursedQuantity}")
        }

        // Approve first
        requisition.approve(storesManager, approveDto.approvedQuantity)

        // Then disburse
        requisition.disburse(storesManager, disburseDto.disbursedQuantity)

        // Update notes if provided
        if (approveDto.notes != null || disburseDto.notes != null) {
            val combinedNotes = listOfNotNull(approveDto.notes, disburseDto.notes).joinToString("\n")
            requisition.notes = if (requisition.notes.isNullOrBlank()) {
                combinedNotes
            } else {
                "${requisition.notes}\nStores: $combinedNotes"
            }
        }

        val savedRequisition = partsRequisitionRepository.save(requisition)

        // Notify technician
        /*webSocketHandler.broadcastToUser(
            requisition.requestedBy.employeeId!!,
            "PARTS_DISBURSED",
            savedRequisition.requisitionId
        )*/

        logger.info("Requisition approved and disbursed: $requisitionId")
        return savedRequisition.toResponseDto()
    }


    fun approveRequisition(
        requisitionId: UUID,
        approveDto: ApprovePartRequisitionDto,
        storesManagerId: UUID
    ): PartRequisitionResponseDto {
        logger.info("Approving requisition: $requisitionId")

        val requisition = partsRequisitionRepository.findById(requisitionId)
            .orElseThrow { EntityNotFoundException("Requisition not found: $requisitionId") }
        val storesManager = employeeRepository.findById(storesManagerId)
            .orElseThrow { EntityNotFoundException("Employee not found: $storesManagerId") }
        // Validate permissions
        if (!hasStoresPermission(storesManager)) {
            throw IllegalStateException("Employee does not have stores management permissions")
        }

        if (!requisition.canApprove()) {
            throw IllegalStateException("Cannot approve requisition in status: ${requisition.status}")
        }

        requisition.approve(storesManager, approveDto.approvedQuantity)

        if (approveDto.notes != null) {
            requisition.notes = if (requisition.notes.isNullOrBlank()) {
                approveDto.notes
            } else {
                "${requisition.notes}\nStores: ${approveDto.notes}"
            }
        }

        val savedRequisition = partsRequisitionRepository.save(requisition)

        // Notify technician
       /* webSocketHandler.broadcastToUser(
            requisition.requestedBy.employeeId!!,
            "PARTS_APPROVED",
            savedRequisition.requisitionId
        )*/

        logger.info("Requisition approved: $requisitionId")
        return savedRequisition.toResponseDto()
    }

    fun markAsNotAvailable(
        requisitionId: UUID,
        notAvailableDto: MarkAsNotAvailableDto,
        storesManagerId: UUID
    ): PartRequisitionResponseDto {
        logger.info("Marking requisition as not available: $requisitionId")

        val requisition = partsRequisitionRepository.findById(requisitionId)
            .orElseThrow { EntityNotFoundException("Requisition not found: $requisitionId") }

        val storesManager = employeeRepository.findById(storesManagerId)
            .orElseThrow { EntityNotFoundException("Employee not found: $storesManagerId") }

        // Validate permissions
        if (!hasStoresPermission(storesManager)) {
            throw IllegalStateException("Employee does not have stores management permissions")
        }

        requisition.markAsNotAvailable(storesManager, notAvailableDto.reason)

        if (notAvailableDto.notes != null) {
            requisition.notes = if (requisition.notes.isNullOrBlank()) {
                notAvailableDto.notes
            } else {
                "${requisition.notes}\nStores: ${notAvailableDto.notes}"
            }
        }

        val savedRequisition = partsRequisitionRepository.save(requisition)

        // Notify technician and service advisor
       /* webSocketHandler.broadcastToUser(
            requisition.requestedBy.employeeId!!,
            "PARTS_NOT_AVAILABLE",
            savedRequisition.requisitionId
        )*/

        logger.info("Requisition marked as not available: $requisitionId")
        return savedRequisition.toResponseDto()
    }

    // ===== QUERY OPERATIONS =====

    fun getRequisitionsByJobCard(jobCardId: UUID): List<PartRequisitionResponseDto> {
        logger.info("Fetching requisitions for job card: $jobCardId")

        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job card not found: $jobCardId") }

        return partsRequisitionRepository.findByJobCardOrderByRequestedAtAsc(jobCard)
            .map { it.toResponseDto() }
    }

    fun getRequisitionsByTechnician(technicianId: UUID): List<PartRequisitionResponseDto> {
        logger.info("Fetching requisitions for technician: $technicianId")

        val technician = employeeRepository.findById(technicianId)
            .orElseThrow { EntityNotFoundException("Employee not found: $technicianId") }

        return partsRequisitionRepository.findByRequestedByOrderByRequestedAtDesc(technician)
            .map { it.toResponseDto() }
    }

    fun getUsedPartsForInvoicing(jobCardId: UUID): List<PartRequisitionResponseDto> {
        logger.info("Fetching used parts for invoicing job card: $jobCardId")

        val jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow { EntityNotFoundException("Job card not found: $jobCardId") }

        return partsRequisitionRepository.findByJobCardAndStatusIn(
            jobCard,
            listOf(PartRequisitionStatus.USED, PartRequisitionStatus.PARTIALLY_USED)
        ).map { it.toResponseDto() }
    }

    // ===== HELPER METHODS =====

    private fun validateTechnicianAssignment(jobCard: JobCard, employee: Employee) {
        val isServiceAdvisor = jobCard.serviceAdvisor?.employeeId == employee.employeeId
        val isSupervisor = jobCard.supervisor?.employeeId == employee.employeeId
        val isTechnician = jobCard.technicians.any { it.employeeId?.employeeId == employee.employeeId }

        if (!isServiceAdvisor && !isSupervisor && !isTechnician) {
            throw IllegalStateException("Employee is not assigned to this job card")
        }
    }

    private fun hasStoresPermission(employee: Employee): Boolean {
        return employee.employeeRole in listOf("ADMIN", "STORES", "STORES_MANAGER")
    }

    // ===== STATISTICS & REPORTING =====

    /*fun getRequisitionStatistics(startDate: LocalDateTime, endDate: LocalDateTime): RequisitionStatisticsDto {
        val stats = partsRequisitionRepository.getStatisticsByDateRange(startDate, endDate)
        return RequisitionStatisticsDto(
            totalRequested = stats!!.totalRequested,
            totalDisbursed = stats.totalDisbursed,
            totalUsed = stats.totalUsed,
            totalNotAvailable = stats.totalNotAvailable,
            averageProcessingTime = 0.0 ,// stats.averageProcessingTime,
            mostRequestedProducts = emptyList() //stats.mostRequestedProducts
        )
    }*/
}
