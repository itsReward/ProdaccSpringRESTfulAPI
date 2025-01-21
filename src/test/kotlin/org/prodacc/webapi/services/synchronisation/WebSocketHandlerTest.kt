// src/test/kotlin/org/prodacc/webapi/services/synchronisation/WebSocketHandlerTest.kt
package org.prodacc.webapi.services.synchronisation

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WebSocketHandlerTest {

    @Test
    fun `test broadcastUpdate`() {
        val webSocketHandler = WebSocketHandler()
        val session1 = mock(WebSocketSession::class.java)
        val session2 = mock(WebSocketSession::class.java)
        webSocketHandler.afterConnectionEstablished(session1)
        webSocketHandler.afterConnectionEstablished(session2)

        val updateType = "NEW_JOB_CARD"
        val entity = mapOf("id" to "123", "name" to "Test Job Card")
        val update = WebSocketUpdate(updateType, entity)
        val json = ObjectMapper().writeValueAsString(update)

        webSocketHandler.broadcastUpdate(updateType, entity)

        verify(session1).sendMessage(TextMessage(json))
        verify(session2).sendMessage(TextMessage(json))
    }
}