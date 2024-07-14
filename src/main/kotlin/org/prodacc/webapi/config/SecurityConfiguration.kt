package org.prodacc.webapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * This class configures Spring Security for the application.
 * It uses a stateless approach with JWT (JSON Web Token) based authentication.
 *
 * The configuration defines the following aspects:
 *
 * 1. Disables CSRF (Cross-Site Request Forgery) protection for simplicity (consider enabling in production).
 * 2. Defines authorization rules for different URL patterns:
- Endpoints like "/api/v1/auth", "/api/v1/auth/refresh", and "/error" are publicly accessible (`permitAll()`).
- POST requests to "/api/v1/users/new" and "/error" require the "ROLE_ADMIN" authority.
- All requests under "/api/v1/users/" (any sub-path under "/api/v1/users") require the "ROLE_ADMIN" authority.
- Any other request requires authentication (`authenticated()`).
 * 3. Sets session management policy to `STATELESS`, indicating the application won't use traditional session-based authentication.
 * 4. Uses the injected `AuthenticationProvider` for user authentication (likely a custom implementation using JWT tokens).
 * 5. Adds a custom `JwtAuthenticationFilter` before the default `UsernamePasswordAuthenticationFilter`.
- This ensures JWT token-based authentication is checked first.
 * 6. Builds and returns the configured `DefaultSecurityFilterChain` bean.
*/

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers( "/api/v1/auth", "/api/v1/auth/refresh", "/error")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "api/v1/users/new", "/error")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers("api/v1/users/**")
                    .hasAuthority("ROLE_ADMIN")
                    .anyRequest()
                    .authenticated()

            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()


}