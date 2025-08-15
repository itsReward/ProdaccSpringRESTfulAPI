package org.prodacc.webapi.controllers

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.prodacc.webapi.services.JobCardPartsRequisitionService
import org.prodacc.webapi.services.UserService
import org.prodacc.webapi.services.dataTransferObjects.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/jobcards/parts-requisition")
@CrossOrigin(origins = ["*"])
class JobCardPartsRequisitionController(
    private val partsRequisitionService: JobCardPartsRequisitionService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // ===== TECHNICIAN OPERATIONS =====

    @PostMapping("/request")
    fun requestParts(
        @Valid @RequestBody requestDto: CreatePartRequisitionDto,
        request: HttpServletRequest
    ): ResponseEntity<PartRequisitionResponseDto> {
        return try {
            val requesterId = getCurrentEmployeeId()
            val requisition = partsRequisitionService.requestParts(requestDto, requesterId)

            logger.info("Parts requisition created successfully: ${requisition.requisitionId}")
            ResponseEntity.ok(requisition)
        } catch (e: Exception) {
            logger.error("Error creating parts requisition: ${e.message}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PutMapping("/{requisitionId}/mark-used")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SERVICE_ADVISOR', 'ADMIN')")
    fun markPartsAsUsed(
        @PathVariable requisitionId: UUID,
        @Valid @RequestBody markAsUsedDto: MarkAsUsedDto,
        request: HttpServletRequest
    ): ResponseEntity<PartRequisitionResponseDto> {
        return try {
            val employeeId = getCurrentEmployeeId()
            val requisition = partsRequisitionService.markPartsAsUsed(requisitionId, markAsUsedDto, employeeId)

            logger.info("Parts marked as used successfully: $requisitionId")
            ResponseEntity.ok(requisition)
        } catch (e: Exception) {
            logger.error("Error marking parts as used: ${e.message}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SERVICE_ADVISOR', 'ADMIN')")
    fun getMyRequisitions(request: HttpServletRequest): ResponseEntity<List<PartRequisitionResponseDto>> {
        return try {
            val employeeId = getCurrentEmployeeId()
            val requisitions = partsRequisitionService.getRequisitionsByTechnician(employeeId)

            ResponseEntity.ok(requisitions)
        } catch (e: Exception) {
            logger.error("Error fetching employee requisitions: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // ===== STORES MANAGER OPERATIONS =====

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('STORES', 'ADMIN')")
    fun getPendingRequisitions(): ResponseEntity<List<PartRequisitionResponseDto>> {
        return try {
            val requisitions = partsRequisitionService.getPendingRequisitions()
            ResponseEntity.ok(requisitions)
        } catch (e: Exception) {
            logger.error("Error fetching pending requisitions: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/{requisitionId}/approve-and-disburse")
    @PreAuthorize("hasAnyRole('STORES', 'ADMIN')")
    fun approveAndDisburse(
        @PathVariable requisitionId: UUID,
        @Valid @RequestBody approveAndDisburseDto: ApproveAndDisburseDto,
        request: HttpServletRequest
    ): ResponseEntity<PartRequisitionResponseDto> {
        return try {
            val storesManagerId = getCurrentEmployeeId()
            val requisition = partsRequisitionService.approveAndDisburseRequisition(
                requisitionId = requisitionId,
                approveDto = approveAndDisburseDto.approve,
                disburseDto = approveAndDisburseDto.disburse,
                storesManagerId = storesManagerId
            )

            logger.info("Requisition approved and disbursed successfully: $requisitionId")
            ResponseEntity.ok(requisition)
        } catch (e: Exception) {
            logger.error("Error approving and disbursing requisition: ${e.message}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PutMapping("/{requisitionId}/mark-not-available")
    @PreAuthorize("hasAnyRole('STORES', 'ADMIN')")
    fun markAsNotAvailable(
        @PathVariable requisitionId: UUID,
        @Valid @RequestBody notAvailableDto: MarkAsNotAvailableDto,
        request: HttpServletRequest
    ): ResponseEntity<PartRequisitionResponseDto> {
        return try {
            val storesManagerId = getCurrentEmployeeId()
            val requisition = partsRequisitionService.markAsNotAvailable(requisitionId, notAvailableDto, storesManagerId)

            logger.info("Requisition marked as not available: $requisitionId")
            ResponseEntity.ok(requisition)
        } catch (e: Exception) {
            logger.error("Error marking requisition as not available: ${e.message}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // ===== QUERY OPERATIONS =====

    @GetMapping("/jobcard/{jobCardId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SERVICE_ADVISOR', 'STORES', 'ADMIN')")
    fun getRequisitionsByJobCard(@PathVariable jobCardId: UUID): ResponseEntity<List<PartRequisitionResponseDto>> {
        return try {
            val requisitions = partsRequisitionService.getRequisitionsByJobCard(jobCardId)
            ResponseEntity.ok(requisitions)
        } catch (e: Exception) {
            logger.error("Error fetching requisitions for job card: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/jobcard/{jobCardId}/used-parts")
    @PreAuthorize("hasAnyRole('SERVICE_ADVISOR', 'ADMIN')")
    fun getUsedPartsForInvoicing(@PathVariable jobCardId: UUID): ResponseEntity<List<PartRequisitionResponseDto>> {
        return try {
            val usedParts = partsRequisitionService.getUsedPartsForInvoicing(jobCardId)
            ResponseEntity.ok(usedParts)
        } catch (e: Exception) {
            logger.error("Error fetching used parts for invoicing: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{requisitionId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SERVICE_ADVISOR', 'STORES', 'ADMIN')")
    fun getRequisitionById(@PathVariable requisitionId: UUID): ResponseEntity<PartRequisitionResponseDto> {
        return try {
            val requisition = partsRequisitionService.getRequisitionById(requisitionId)
            ResponseEntity.ok(requisition)
        } catch (e: Exception) {
            logger.error("Error fetching requisition: ${e.message}", e)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // ===== ANALYTICS & REPORTING =====

    /*@GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORES')")
    fun getRequisitionStatistics(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): ResponseEntity<RequisitionStatisticsDto> {
        return try {
            val start = LocalDateTime.parse(startDate)
            val end = LocalDateTime.parse(endDate)
            val statistics = partsRequisitionService.getRequisitionStatistics(start, end)

            ResponseEntity.ok(statistics)
        } catch (e: Exception) {
            logger.error("Error fetching requisition statistics: ${e.message}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }*/

    // ===== HELPER METHODS =====

    private fun getCurrentEmployeeId(): UUID {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication context found")

        // Assuming your JWT contains the employee ID as the principal or in claims
        val employeeIdString = when (val principal = authentication.principal) {
            is String -> principal // If the principal is directly the employee ID
            is UserDetails -> principal.username // If using UserDetails
            else -> authentication.name // Fallback to authentication name
        }

        return try {
            userService.findUserByUsername(employeeIdString).employeeId!!
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid employee ID format: $employeeIdString", e)
        }
    }
}

// ===== ADDITIONAL REQUEST DTOs =====

data class ApproveAndDisburseDto(
    val approve: ApprovePartRequisitionDto,
    val disburse: DisbursePartRequisitionDto
)

// ===== VALIDATION ANNOTATIONS =====

