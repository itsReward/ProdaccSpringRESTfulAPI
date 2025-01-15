package org.prodacc.webapi.services.databaseSynchronisation

import com.fasterxml.jackson.databind.ObjectMapper
import org.prodacc.webapi.services.TimeSheetService
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
            .setAllowedOrigins("*")
            .addInterceptors(WebSocketAuthInterceptor(jwtAuthenticationHandler))
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
    fun validateToken(token: String): Boolean {
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
