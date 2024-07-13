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

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
): OncePerRequestFilter() {

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