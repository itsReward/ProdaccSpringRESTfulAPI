package org.prodacc.webapi.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.prodacc.webapi.services.TokenService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * This filter intercepts incoming HTTP requests and performs JWT (JSON Web Token) based authentication.
 * It checks for the presence of an "Authorization" header with the "Bearer" prefix followed by a valid JWT token.
 *
 * The filter performs the following steps:
 *
 * 1. Extracts the JWT token from the "Authorization" header if present.
 * 2. Uses the injected `TokenService` to extract the username from the token.
 * 3. If a username is found and authentication hasn't been established yet:
 *    - Fetches user details from the injected `UserDetailsService` using the extracted username.
 *    - Verifies the JWT token's validity using the `TokenService`.
 *    - If the token is valid, updates the Spring Security context with a `UsernamePasswordAuthenticationToken` object:
 *        - The token itself is included.
 *        - User details are retrieved from the `UserDetailsService`.
 *        - Authorities are either extracted from the token using the `TokenService` or retrieved from the user details.
 *        - Request details are attached using `WebAuthenticationDetailsSource`.
 * 4. The filter chain continues processing the request if the token is invalid or no "Authorization" header is present.
 */

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
): OncePerRequestFilter()
{

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()){
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val username = tokenService.extractUsername(jwtToken)

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUserDetails = userDetailsService.loadUserByUsername(username)

            if (tokenService.isValid(jwtToken, foundUserDetails)) {
                updateContext(jwtToken, foundUserDetails, request)
            }

            filterChain.doFilter(request, response)
        }
    }

    private fun updateContext(token: String, userDetails: UserDetails, request: HttpServletRequest) {
        val authorities = tokenService.extractAuthorities(token)
            ?.map { SimpleGrantedAuthority(it) }
            ?: userDetails.authorities

        val authToken = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken
    }



    private fun String?.doesNotContainBearerToken(): Boolean =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String {
        return this.substringAfter("Bearer ")
    }
}

