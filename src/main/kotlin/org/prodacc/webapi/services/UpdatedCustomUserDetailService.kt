/**
 * Updated Custom User Details Service to work with new permission system
 */
package org.prodacc.webapi.services

import org.prodacc.webapi.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


/**
 * Updated User Details Service that supports both old and new permission systems
 * During transition period, this will work with both systems
 */
@Service
class UpdatedCustomUserDetailsService(
    private val userRepository: UserRepository,
    private val permissionService: PermissionService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User with username: $username not found") }

        return user.mapToUserDetails()
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails {
        val authorities = mutableListOf<SimpleGrantedAuthority>()

        // Add legacy role authority for backward compatibility
        this.userRole?.let { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_${role.uppercase()}"))
        }

        // Add new permission-based authorities if user is migrated
        this.id?.let { userId ->
            try {
                val permissions = permissionService.getUserPermissions(userId)
                permissions.forEach { permission ->
                    authorities.add(SimpleGrantedAuthority("PERM_${permission.name!!.uppercase()}"))
                }

                val roles = permissionService.getUserRoles(userId)
                roles.forEach { role ->
                    authorities.add(SimpleGrantedAuthority("ROLE_${role.name!!.uppercase()}"))
                }
            } catch (e: Exception) {
                // If new permission system fails, fall back to legacy role
                // This ensures system still works during transition
                logger.warn("Failed to load new permissions for user $userId, using legacy role", e)
            }
        }

        return User.builder()
            .username(this.username)
            .password(this.password)
            .authorities(authorities)
            .build()
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(UpdatedCustomUserDetailsService::class.java)
    }
}