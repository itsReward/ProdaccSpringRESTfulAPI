/**
 * Updated Custom User Details Service to work with new permission system
 */
package org.prodacc.webapi.services

import org.springframework.stereotype.Service

/**
 * Updated Custom User Details Service to work with new permission system
 */
@Service
class UpdatedCustomUserDetailsService(
    private val userRepository: org.prodacc.webapi.repositories.UserRepository,
    private val permissionService: PermissionService
) : org.springframework.security.core.userdetails.UserDetailsService {

    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { org.springframework.security.core.userdetails.UsernameNotFoundException("User with username: $username not found") }

        return user.mapToUserDetails()
    }

    private fun org.prodacc.webapi.models.User.mapToUserDetails(): org.springframework.security.core.userdetails.UserDetails {
        val authorities = mutableListOf<org.springframework.security.core.authority.SimpleGrantedAuthority>()

        // Add legacy role authority for backward compatibility
        this.userRole?.let { role ->
            authorities.add(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_${role.uppercase()}"))
        }

        // Add new permission-based authorities if user is migrated
        this.id?.let { userId ->
            try {
                val permissions = permissionService.getUserPermissions(userId)
                permissions.forEach { permission ->
                    authorities.add(org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_${permission.name!!.uppercase()}"))
                }

                val roles = permissionService.getUserRoles(userId)
                roles.forEach { role ->
                    authorities.add(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_${role.name!!.uppercase()}"))
                }
            } catch (e: Exception) {
                // If new permission system fails, fall back to legacy role
                // This ensures system still works during transition
                logger.warn("Failed to load new permissions for user $userId, using legacy role", e)
            }
        }

        return org.springframework.security.core.userdetails.User.builder()
            .username(this.username)
            .password(this.password)
            .authorities(authorities)
            .build()
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(UpdatedCustomUserDetailsService::class.java)
    }
}