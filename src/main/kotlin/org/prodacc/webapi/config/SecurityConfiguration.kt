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
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
    private val corsConfigurationSource: CorsConfigurationSource
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource) } // Enable CORS
            .authorizeHttpRequests { auth ->
                auth
                    // Allow OPTIONS requests for CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Allow swagger endpoints
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs").permitAll()

                    // Allow WebSocket connections
                    .requestMatchers("/websocket/**").permitAll()

                    // IMPORTANT: Allow authentication endpoints WITHOUT /api prefix
                    .requestMatchers("/auth", "/auth/refresh", "/error").permitAll()

                    // Also allow with /api prefix (in case your frontend uses it)
                    .requestMatchers("/api/auth", "/api/auth/refresh", "/api/error").permitAll()
                    .requestMatchers("/api/v1/auth", "/api/v1/auth/refresh", "/api/v1/error").permitAll()

                    // User management endpoints
                    .requestMatchers(HttpMethod.GET, "/users/findByUserName/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                    .requestMatchers(HttpMethod.POST, "/users/new", "/error").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/users/**").hasAuthority("ROLE_ADMIN")

                    // Also allow user endpoints with /api prefix
                    .requestMatchers(HttpMethod.GET, "/api/users/findByUserName/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/users/new").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}