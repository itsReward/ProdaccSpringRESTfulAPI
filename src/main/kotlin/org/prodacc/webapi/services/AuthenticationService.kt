package org.prodacc.webapi.services

import org.prodacc.webapi.config.JwtProperties
import org.prodacc.webapi.controllers.AuthenticationRequest
import org.prodacc.webapi.controllers.AuthenticationResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.util.*

/**
 * This service handles user authentication and token generation.
 * It relies on injected dependencies for authentication and token services.
 *
 * The service offers the following functionality:
 *
 * 1. `authentication(authRequest)`: Performs user authentication and returns an authentication response.
- Takes an `AuthenticationRequest` object containing username and password as input.
- Loads user details from the injected `CustomUserDetailsService` using the provided username.
- Attempts to authenticate the user using the injected `AuthenticationManager`.
- Logs successful authentication attempts.
- Catches any exceptions during authentication and re-throws them.
- On successful authentication, generates an access token using the `TokenService`:
- User details and expiration time (based on configured `jwtProperties`) are provided.
- User authorities are included as an additional claim in the token.
- Returns an `AuthenticationResponse` object containing the generated access token.
 */
@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)



    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {

        val user = userDetailsService.loadUserByUsername(authRequest.username)

        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.username,
                    authRequest.password
                )
            )
            logger.info("Authentication Successful")
        }catch (e: Exception) {
            logger.error("Authentication Error", e)
            throw e
        }

        val accessToken = tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
            additionalClaims = mapOf("authorities" to user.authorities.map { it.authority }),
        )

        return AuthenticationResponse(accessToken)
    }


}
