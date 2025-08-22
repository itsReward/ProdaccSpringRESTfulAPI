package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.*
import org.prodacc.webapi.services.PermissionService
import org.prodacc.webapi.services.dataTransferObjects.permission.PermissionCheckResult
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

/**
 * REST Controller for managing permissions and roles
 */
@RestController
@RequestMapping("/api/permissions")
class PermissionController(
    private val permissionService: PermissionService
) {

    /**
     * Grant a temporary permission to a user
     */
    @PostMapping("/grant")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'permissions', 'grant')")
    fun grantPermission(
        @RequestBody request: GrantPermissionRequest,
        authentication: Authentication
    ): ResponseEntity<UserPermission> {
        val grantedBy = extractUserId(authentication)

        val userPermission = permissionService.grantTemporaryPermission(
            userId = request.userId,
            permissionId = request.permissionId,
            grantedBy = grantedBy,
            expiresAt = request.expiresAt,
            context = request.context,
            reason = request.reason
        )

        return ResponseEntity.ok(userPermission)
    }

    /**
     * Revoke a permission from a user
     */
    @DeleteMapping("/revoke")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'permissions', 'revoke')")
    fun revokePermission(
        @RequestBody request: RevokePermissionRequest,
        authentication: Authentication
    ): ResponseEntity<String> {
        val revokedBy = extractUserId(authentication)

        permissionService.revokePermission(
            userId = request.userId,
            permissionId = request.permissionId,
            revokedBy = revokedBy
        )

        return ResponseEntity.ok("Permission revoked successfully")
    }

    /**
     * Assign a role to a user
     */
    @PostMapping("/assign-role")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'permissions', 'grant')")
    fun assignRole(
        @RequestBody request: AssignRoleRequest,
        authentication: Authentication
    ): ResponseEntity<UserRole> {
        val assignedBy = extractUserId(authentication)

        val userRole = permissionService.assignRole(
            userId = request.userId,
            roleId = request.roleId,
            assignedBy = assignedBy,
            expiresAt = request.expiresAt
        )

        return ResponseEntity.ok(userRole)
    }

    /**
     * Revoke a role from a user
     */
    @DeleteMapping("/revoke-role")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'permissions', 'revoke')")
    fun revokeRole(
        @RequestBody request: RevokeRoleRequest,
        authentication: Authentication
    ): ResponseEntity<String> {
        val revokedBy = extractUserId(authentication)

        permissionService.revokeRole(
            userId = request.userId,
            roleId = request.roleId,
            revokedBy = revokedBy
        )

        return ResponseEntity.ok("Role revoked successfully")
    }

    /**
     * Get all permissions for a user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'user', 'read') or @permissionEvaluator.canAccessOwnResource(authentication, #userId)")
    fun getUserPermissions(@PathVariable userId: UUID): ResponseEntity<UserPermissionsResponse> {
        val permissions = permissionService.getUserPermissions(userId)
        val roles = permissionService.getUserRoles(userId)

        val response = UserPermissionsResponse(
            userId = userId,
            permissions = permissions,
            roles = roles
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Get permission audit trail for a user
     */
    @GetMapping("/audit/{userId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'user', 'read') or @permissionEvaluator.canAccessOwnResource(authentication, #userId)")
    fun getPermissionAuditTrail(@PathVariable userId: UUID): ResponseEntity<List<PermissionAuditLog>> {
        val auditTrail = permissionService.getPermissionAuditTrail(userId)
        return ResponseEntity.ok(auditTrail)
    }

    /**
     * Check if user has a specific permission
     */
    @PostMapping("/check")
    fun checkPermission(
        @RequestBody request: CheckPermissionRequest,
        authentication: Authentication
    ): ResponseEntity<PermissionCheckResult> {
        val userId = request.userId ?: extractUserId(authentication)

        val result = permissionService.checkPermissionWithDetails(
            userId = userId,
            resource = request.resource,
            action = request.action,
            context = request.context
        )

        return ResponseEntity.ok(result)
    }

    /**
     * Migrate a user to the new permission system
     */
    @PostMapping("/migrate/{userId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'system', 'configure')")
    fun migrateUser(@PathVariable userId: UUID): ResponseEntity<String> {
        // Implementation would go here
        return ResponseEntity.ok("User migration completed")
    }

    /**
     * Clean up expired permissions
     */
    @PostMapping("/cleanup")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'system', 'configure')")
    fun cleanupExpiredPermissions(): ResponseEntity<String> {
        permissionService.cleanupExpiredPermissions()
        return ResponseEntity.ok("Cleanup completed")
    }

    private fun extractUserId(authentication: Authentication): UUID {
        // Extract user ID from authentication - adjust based on your setup
        return UUID.fromString(authentication.name) // Placeholder
    }
}

/**
 * Data Transfer Objects for API requests/responses
 */

data class GrantPermissionRequest(
    val userId: UUID,
    val permissionId: UUID,
    val expiresAt: Instant? = null,
    val context: Map<String, Any>? = null,
    val reason: String? = null
)

data class RevokePermissionRequest(
    val userId: UUID,
    val permissionId: UUID
)

data class AssignRoleRequest(
    val userId: UUID,
    val roleId: UUID,
    val expiresAt: Instant? = null
)

data class RevokeRoleRequest(
    val userId: UUID,
    val roleId: UUID
)

data class CheckPermissionRequest(
    val userId: UUID? = null, // If null, check for current user
    val resource: String,
    val action: String,
    val context: Map<String, Any>? = null
)

data class UserPermissionsResponse(
    val userId: UUID,
    val permissions: List<Permission>,
    val roles: List<Role>
)

