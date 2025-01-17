package org.prodacc.webapi.services

import org.prodacc.webapi.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = org.prodacc.webapi.models.User


/**
 * Maps the username and the password from the  User entity to spring UserDetails Service
 * @see ApplicationUser
*/

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
): UserDetailsService
{

    override fun loadUserByUsername(username: String): UserDetails {
        val details = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User with username: $username not found") }
            .mapToUserDetails()
        return details
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails {
        val user = User.builder()
            .username(this.username)
            .password(this.password)
            .authorities(SimpleGrantedAuthority("ROLE_${this.userRole!!.uppercase()}"))
            .build()
        return user
    }

}