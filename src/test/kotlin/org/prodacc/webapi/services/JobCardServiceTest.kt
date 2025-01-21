package org.prodacc.webapi.services

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.prodacc.webapi.models.JobCard
import org.prodacc.webapi.repositories.*
import org.prodacc.webapi.services.dataTransferObjects.NewJobCard
import org.prodacc.webapi.services.synchronisation.WebSocketHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

class JobCardServiceTest {

    private val jobCardRepository = mock(JobCardRepository::class.java)
    private val webSocketHandler = mock(WebSocketHandler::class.java)
    private val jobCardService = JobCardService(
        jobCardRepository,
        mock(ClientRepository::class.java),
        mock(VehicleRepository::class.java),
        mock(EmployeeRepository::class.java),
        mock(TimesheetRepository::class.java),
        mock(ServiceChecklistRepository::class.java),
        mock(ControlChecklistRepository::class.java),
        mock(VehicleStateChecklistRepository::class.java),
        mock(JobCardStatusRepository::class.java),
        mock(JobCardReportsRepository::class.java),
        mock(JobCardTechniciansRepository::class.java),
        webSocketHandler
    )

    @Test
    fun `test newJobCard broadcasts update`() {
        val newJobCard = NewJobCard(
            vehicleId = UUID.fromString("fa5e2006-10fb-42e8-8c8c-5be7e0f9ec1f"),
            serviceAdvisorId = UUID.fromString("6a36a39f-2816-489c-beba-c0ce07366745"),
            supervisorId = UUID.fromString("27116468-4418-4f0e-9861-dc8c09488ffa"),
            dateAndTimeIn = LocalDateTime.now(),
            jobCardStatus = "OPEN",
            estimatedTimeOfCompletion = LocalDateTime.now(),
            dateAndTimeFrozen = null,
            dateAndTimeClosed = null,
            priority = true,
            jobCardDeadline = LocalDateTime.now(),
        )
        val responseJobCard = jobCardService.newJobCard(newJobCard)

        verify(webSocketHandler).broadcastUpdate(eq("NEW_JOB_CARD"), any())
    }

    @Test
    fun `test deleteJobCard broadcasts update`() {
        val jobCardId = UUID.randomUUID()
        `when`(jobCardRepository.existsById(jobCardId)).thenReturn(true)
        `when`(jobCardRepository.findById(jobCardId)).thenReturn(Optional.of(mock(JobCard::class.java)))

        val response = jobCardService.deleteJobCard(jobCardId)

        assertEquals(ResponseEntity("Job Card Deleted successfully", HttpStatus.OK), response)
        verify(webSocketHandler).broadcastUpdate("DELETE_JOB_CARD", jobCardId)
    }
}