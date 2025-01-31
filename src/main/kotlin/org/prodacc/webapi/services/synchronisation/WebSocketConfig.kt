package org.prodacc.webapi.services.synchronisation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.prodacc.webapi.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.lang.Nullable
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSocket
class WebSocketConfig(
    @Autowired private val jwtAuthenticationHandler: JwtAuthenticationHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WebSocketHandler(), "/websocket")
            .addInterceptors(WebSocketAuthInterceptor(jwtAuthenticationHandler))
            .setAllowedOrigins("*")
    }

}

@Component
class WebSocketHandler : TextWebSocketHandler() {
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
        logger.info("WebSocket connection established: ${session.id}")
        logger.info("Current WebSocket sessions: ${sessions.keys}")
        logger.info("Session URI: ${session.uri}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            // Handle incoming messages
            val payload = message.payload
            logger.info("Received message: $payload from session: ${session.id}")

            if (payload == "ping") {
                session.sendMessage(TextMessage("pong"))
                return
            }


            // Process the payload
            val objectMapper = ObjectMapper()
            val webSocketUpdate = objectMapper.readValue(payload, WebSocketUpdate::class.java)
            val updateType = webSocketUpdate.type
            val entity = webSocketUpdate.data

            // Call broadcastUpdate function
            broadcastUpdate(updateType, entity)
        } catch (e: Exception) {
            logger.error("Error handling message from session ${session.id}", e)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
        logger.info("WebSocket connection closed: ${session.id}")
    }

    fun broadcastUpdate(updateType: String, entity: Any) {
        logger.info("broadcasting message: update type: $updateType, entity: $entity")
        val update = WebSocketUpdate(updateType, entity.toString())
        val json = ObjectMapper().writeValueAsString(update)
        logger.info(json)

        logger.info(sessions.values.toString())
        sessions.values.forEach { session ->
            try {
                session.sendMessage(TextMessage(json))
                logger.info("Message sent to session ${session.id}")
            } catch (e: Exception) {
                logger.error("Error sending message to session ${session.id}", e)
            }
        }
    }
}

// Data class for WebSocket updates
data class WebSocketUpdate (
    @get:JsonProperty("type")
    val type: String,
    @get:JsonProperty("data")
    val data: String
) {
    @JsonCreator
    constructor() : this("", "")
}

class WebSocketAuthInterceptor(
    private val jwtAuthenticationHandler: JwtAuthenticationHandler
) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: org.springframework.web.socket.WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        // Extract token from query parameter
        val uri = request.uri
        val token = uri.query?.split("&")
            ?.find { it.startsWith("token=") }
            ?.substring("token=".length)

        return if (token != null) {
            try {
                val isValid = jwtAuthenticationHandler.validateToken(token)
                if (isValid) {
                    // Store token in attributes for later use if needed
                    attributes["token"] = token
                }
                isValid
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: org.springframework.web.socket.WebSocketHandler,
        @Nullable exception: java.lang.Exception?
    ) {
        // Implementation of after handshake - usually empty as most logic is in beforeHandshake
    }
}

@Component
class JwtAuthenticationHandler(
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsService
) {
    val logger = LoggerFactory.getLogger(JwtAuthenticationHandler::class.java)

    fun validateToken(token: String): Boolean {
        logger.info("Validating token: $token")
        return try {
            // Extract username from token
            val username = tokenService.extractUsername(token) ?: return false

            // Load user details
            val userDetails = userDetailsService.loadUserByUsername(username)

            // Validate token
            tokenService.isValid(token, userDetails)
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthorities(token: String): Collection<GrantedAuthority>? {
        return try {
            tokenService.extractAuthorities(token)
                ?.map { SimpleGrantedAuthority(it) }
        } catch (e: Exception) {
            null
        }
    }

}

