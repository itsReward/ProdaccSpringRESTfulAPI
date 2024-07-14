package org.prodacc.webapi.config

import org.prodacc.webapi.repositories.UserRepository
import org.prodacc.webapi.services.CustomUserDetailsService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Configuration class that sets up user details service that fetches user details, sets up the password encoder, the
 * spring framework authentication provider, and the spring framework authentication manager
 * @see UserDetailsService
 * @see CustomUserDetailsService
 * @see
*/

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService =
        CustomUserDetailsService(userRepository)

    @Bean
    fun encoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(userRepository))
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager



}