package org.prodacc.webapi.services.databaseSynchronisation

import com.fasterxml.jackson.databind.ObjectMapper
import org.prodacc.webapi.services.TimeSheetService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WebSocketHandler(), "/websocket")
            .setAllowedOrigins("*")
    }

}

@Component
class WebSocketHandler : TextWebSocketHandler() {
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val logger = LoggerFactory.getLogger(TimeSheetService::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
        logger.info("WebSocket connection established: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // Handle incoming messages if needed
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
        logger.info("WebSocket connection closed: ${session.id}")
    }

    fun broadcastUpdate(updateType: String, entity: Any) {
        val update = WebSocketUpdate(updateType, entity)
        val json = ObjectMapper().writeValueAsString(update)

        sessions.values.forEach { session ->
            try {
                session.sendMessage(TextMessage(json))
            } catch (e: Exception) {
                logger.error("Error sending message to session ${session.id}", e)
            }
        }
    }
}

// Data class for WebSocket updates
data class WebSocketUpdate(
    val type: String,
    val data: Any
)
