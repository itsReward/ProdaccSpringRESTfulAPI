package org.prodacc.webapi.config

import org.prodacc.webapi.services.PermissionService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Permission Evaluator Bean for use in @PreAuthorize annotations
 * This allows us to use expressions like: @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'jobcard', 'create')")
 */
@Component("permissionEvaluator")
class PermissionEvaluator(
    private val permissionService: PermissionService
) {

    /**
     * Main permission check method for use in @PreAuthorize
     */
    fun hasPermission(
        authentication: Authentication,
        resource: String,
        action: String,
        context: Map<String, Any>? = null
    ): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        return permissionService.hasPermission(userId, resource, action, context)
    }

    /**
     * Check permission with context for things like amount limits
     */
    fun hasPermissionWithContext(
        authentication: Authentication,
        resource: String,
        action: String,
        contextKey: String,
        contextValue: Any
    ): Boolean {
        val context = mapOf(contextKey to contextValue)
        return hasPermission(authentication, resource, action, context)
    }

    /**
     * Check if user has any of the specified permissions
     */
    fun hasAnyPermission(
        authentication: Authentication,
        permissions: List<Pair<String, String>> // List of (resource, action) pairs
    ): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        return permissions.any { (resource, action) ->
            permissionService.hasPermission(userId, resource, action)
        }
    }

    /**
     * Check if user has all of the specified permissions
     */
    fun hasAllPermissions(
        authentication: Authentication,
        permissions: List<Pair<String, String>>
    ): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        return permissions.all { (resource, action) ->
            permissionService.hasPermission(userId, resource, action)
        }
    }

    /**
     * Check if user has a specific role
     */
    fun hasRole(authentication: Authentication, roleName: String): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        val userRoles = permissionService.getUserRoles(userId)
        return userRoles.any { it.name == roleName }
    }

    /**
     * Check if user has any of the specified roles
     */
    fun hasAnyRole(authentication: Authentication, roleNames: List<String>): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        val userRoles = permissionService.getUserRoles(userId)
        val userRoleNames = userRoles.map { it.name }
        return roleNames.any { it in userRoleNames }
    }

    /**
     * Check if user can access their own resources
     */
    fun canAccessOwnResource(authentication: Authentication, resourceOwnerId: UUID): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)
        return userId == resourceOwnerId
    }

    /**
     * Check if user can access job card (own, assigned, or all)
     */
    fun canAccessJobCard(authentication: Authentication, jobCardId: UUID): Boolean {
        val userId = extractUserIdFromAuthentication(authentication)

        // Check if user has full job card access
        if (permissionService.hasPermission(userId, "jobcard", "read.all")) {
            return true
        }

        // Check if user can read own job cards (you'll need to implement this logic)
        if (permissionService.hasPermission(userId, "jobcard", "read.own")) {
            // Add logic to check if this job card belongs to the user
            return isJobCardOwnedByUser(jobCardId, userId)
        }

        // Check if user can read assigned job cards
        if (permissionService.hasPermission(userId, "jobcard", "read.assigned")) {
            // Add logic to check if this job card is assigned to the user
            return isJobCardAssignedToUser(jobCardId, userId)
        }

        return false
    }

    private fun extractUserIdFromAuthentication(authentication: Authentication): UUID {
        // This depends on how you store user information in your JWT/Authentication
        // You might need to adjust this based on your actual authentication setup
        return when (val principal = authentication.principal) {
            is org.springframework.security.core.userdetails.User -> {
                // If you store user ID in the username or need to look it up
                UUID.fromString(principal.username) // Adjust this based on your setup
            }
            is UUID -> principal
            is String -> UUID.fromString(principal)
            else -> throw IllegalArgumentException("Cannot extract user ID from authentication: $principal")
        }
    }

    // You'll need to implement these methods based on your JobCard repository
    private fun isJobCardOwnedByUser(jobCardId: UUID, userId: UUID): Boolean {
        // Implement logic to check if job card is created by this user (service advisor)
        // This would typically involve checking the JobCard's serviceAdvisor field
        return false // Placeholder
    }

    private fun isJobCardAssignedToUser(jobCardId: UUID, userId: UUID): Boolean {
        // Implement logic to check if job card is assigned to this user (technician)
        // This would typically involve checking the JobCardTechnicians table
        return false // Placeholder
    }
}