package org.prodacc.webapi.services

import org.prodacc.webapi.config.JwtProperties
import org.prodacc.webapi.controllers.AuthenticationRequest
import org.prodacc.webapi.controllers.AuthenticationResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)



    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
       logger.info("Authentication Request for user ${authRequest.username}")



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


        val user = userDetailsService.loadUserByUsername(authRequest.username)
        logger.info("Loaded user details - Username: ${user.username}, Encoded Password: ${user.password}")

        logger.info("Raw password from request: ${authRequest.password}")
        logger.info("Encoded password from request: ${passwordEncoder.encode(authRequest.password)}")


        val accessToken = tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthenticationResponse(accessToken)
    }


}
