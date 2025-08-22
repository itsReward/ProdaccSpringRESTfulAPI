package org.prodacc.webapi.services

import org.prodacc.webapi.models.*
import org.prodacc.webapi.repositories.*
import org.prodacc.webapi.services.dataTransferObjects.*
import org.prodacc.webapi.services.dataTransferObjects.permission.PermissionCheckResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
@Transactional
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userPermissionRepository: UserPermissionRepository,
    private val permissionAuditLogRepository: PermissionAuditLogRepository,
    val userRepository: UserRepository // Made public for controller access
) {

    private val logger = LoggerFactory.getLogger(PermissionService::class.java)

    /**
     * Core permission checking method
     * Checks both role-based and direct user permissions
     */
    fun hasPermission(
        userId: UUID,
        resource: String,
        action: String,
        context: Map<String, Any>? = null
    ): Boolean {
        val result = checkPermissionWithDetails(userId, resource, action, context)
        return result.hasPermission
    }

    /**
     * Enhanced permission check that returns detailed information
     * about where the permission came from
     */
    fun checkPermissionWithDetails(
        userId: UUID,
        resource: String,
        action: String,
        context: Map<String, Any>? = null
    ): PermissionCheckResult {
        logger.debug("Checking permission for user: $userId, resource: $resource, action: $action")

        val now = Instant.now()

        // 1. Check direct user permissions first (highest priority)
        val directPermission = userPermissionRepository.findActiveUserPermission(userId, resource, action, now)
        if (directPermission.isPresent) {
            val userPerm = directPermission.get()
            if (evaluateContext(userPerm.contextData, context)) {
                logger.debug("Permission granted via direct user permission: ${userPerm.permission!!.name}")
                return PermissionCheckResult(
                    hasPermission = true,
                    source = "direct",
                    permissionName = userPerm.permission.name,
                    context = userPerm.contextData
                )
            }
        }

        // 2. Check role-based permissions
        val userRoles = userRoleRepository.findActiveRolesByUserId(userId, now)

        for (userRole in userRoles) {
            val role = userRole.role

            // Check direct role permissions
            val rolePermission = role!!.permissions.find {
                it.resource == resource && it.action == action
            }
            if (rolePermission != null) {
                logger.debug("Permission granted via role permission: ${role.name} -> ${rolePermission.name}")
                return PermissionCheckResult(
                    hasPermission = true,
                    source = "role",
                    roleName = role.name,
                    permissionName = rolePermission.name
                )
            }

            // Check permission group permissions
            for (permissionGroup in role.permissionGroups) {
                val groupPermission = permissionGroup.permissions.find {
                    it.resource == resource && it.action == action
                }
                if (groupPermission != null) {
                    logger.debug("Permission granted via role group: ${role.name} -> ${permissionGroup.name} -> ${groupPermission.name}")
                    return PermissionCheckResult(
                        hasPermission = true,
                        source = "role",
                        roleName = role.name,
                        permissionName = groupPermission.name
                    )
                }
            }
        }

        logger.debug("Permission denied for user: $userId, resource: $resource, action: $action")
        return PermissionCheckResult(hasPermission = false, source = "none")
    }

    /**
     * Grant a temporary permission to a user
     */
    fun grantTemporaryPermission(
        userId: UUID,
        permissionId: UUID,
        grantedBy: UUID,
        expiresAt: Instant?,
        context: Map<String, Any>? = null,
        reason: String? = null
    ): UserPermission {
        logger.info("Granting temporary permission: $permissionId to user: $userId by: $grantedBy")

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        val permission = permissionRepository.findById(permissionId)
            .orElseThrow { IllegalArgumentException("Permission not found: $permissionId") }

        val grantedByUser = userRepository.findById(grantedBy)
            .orElseThrow { IllegalArgumentException("Granting user not found: $grantedBy") }

        // Check if granting user has permission to grant this permission
        if (!hasPermission(grantedBy, "permissions", "grant")) {
            throw SecurityException("User $grantedBy does not have permission to grant permissions")
        }

        // Check if user already has this permission
        val existingPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
        if (existingPermission.isPresent) {
            val existing = existingPermission.get()
            if (existing.isValid()) {
                throw IllegalStateException("User already has this permission")
            }
            // Reactivate if expired
            existing.isActive = true
            return userPermissionRepository.save(existing)
        }

        val contextWithReason = if (reason != null) {
            (context ?: emptyMap()) + mapOf("reason" to reason)
        } else {
            context
        }

        val userPermission = UserPermission(
            id = UserPermissionId(userId, permissionId),
            user = user,
            permission = permission,
            grantedBy = grantedByUser,
            expiresAt = expiresAt,
            contextData = contextWithReason
        )

        val savedPermission = userPermissionRepository.save(userPermission)

        // Log the action
        logPermissionChange(
            user = user,
            action = "granted",
            permissionType = "direct_permission",
            permissionIdentifier = permission!!.name!!,
            grantedBy = grantedByUser,
            contextData = contextWithReason
        )

        return savedPermission
    }

    /**
     * Assign a role to a user
     */
    fun assignRole(
        userId: UUID,
        roleId: UUID,
        assignedBy: UUID,
        expiresAt: Instant? = null
    ): UserRole {
        logger.info("Assigning role: $roleId to user: $userId by: $assignedBy")

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        val role = roleRepository.findById(roleId)
            .orElseThrow { IllegalArgumentException("Role not found: $roleId") }

        val assignedByUser = userRepository.findById(assignedBy)
            .orElseThrow { IllegalArgumentException("Assigning user not found: $assignedBy") }

        // Check if assigning user has permission to assign roles
        if (!hasPermission(assignedBy, "permissions", "grant")) {
            throw SecurityException("User $assignedBy does not have permission to assign roles")
        }

        // Check if user already has this role
        val existingRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
        if (existingRole.isPresent) {
            val existing = existingRole.get()
            if (existing.isValid()) {
                throw IllegalStateException("User already has this role")
            }
            // Reactivate if expired
            existing.isActive = true
            return userRoleRepository.save(existing)
        }

        val userRole = UserRole(
            id = UserRoleId(userId, roleId),
            user = user,
            role = role,
            assignedBy = assignedByUser,
            expiresAt = expiresAt
        )

        val savedRole = userRoleRepository.save(userRole)

        // Log the action
        logPermissionChange(
            user = user,
            action = "granted",
            permissionType = "role",
            permissionIdentifier = role.name!!,
            grantedBy = assignedByUser
        )

        return savedRole
    }

    /**
     * Revoke a permission from a user
     */
    fun revokePermission(
        userId: UUID,
        permissionId: UUID,
        revokedBy: UUID
    ) {
        logger.info("Revoking permission: $permissionId from user: $userId by: $revokedBy")

        val userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
            .orElseThrow { IllegalArgumentException("User permission not found") }

        val revokedByUser = userRepository.findById(revokedBy)
            .orElseThrow { IllegalArgumentException("Revoking user not found: $revokedBy") }

        // Check if revoking user has permission to revoke permissions
        if (!hasPermission(revokedBy, "permissions", "revoke")) {
            throw SecurityException("User $revokedBy does not have permission to revoke permissions")
        }

        userPermission.isActive = false
        userPermissionRepository.save(userPermission)

        // Log the action
        logPermissionChange(
            user = userPermission.user!!,
            action = "revoked",
            permissionType = "direct_permission",
            permissionIdentifier = userPermission.permission!!.name!!,
            grantedBy = revokedByUser
        )
    }

    /**
     * Revoke a role from a user
     */
    fun revokeRole(
        userId: UUID,
        roleId: UUID,
        revokedBy: UUID
    ) {
        logger.info("Revoking role: $roleId from user: $userId by: $revokedBy")

        val userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
            .orElseThrow { IllegalArgumentException("User role not found") }

        val revokedByUser = userRepository.findById(revokedBy)
            .orElseThrow { IllegalArgumentException("Revoking user not found: $revokedBy") }

        // Check if revoking user has permission to revoke roles
        if (!hasPermission(revokedBy, "permissions", "revoke")) {
            throw SecurityException("User $revokedBy does not have permission to revoke roles")
        }

        userRole.isActive = false
        userRoleRepository.save(userRole)

        // Log the action
        logPermissionChange(
            user = userRole.user!!,
            action = "revoked",
            permissionType = "role",
            permissionIdentifier = userRole.role!!.name!!,
            grantedBy = revokedByUser
        )
    }

    /**
     * Get all permissions for a user (both direct and role-based)
     */
    fun getUserPermissions(userId: UUID): List<Permission> {
        val now = Instant.now()
        val permissions = mutableListOf<Permission>()

        // Get direct permissions
        val directPermissions = userPermissionRepository.findActivePermissionsByUserId(userId, now)
        permissions.addAll(directPermissions.map { it.permission!! } )

        // Get role-based permissions
        val userRoles = userRoleRepository.findActiveRolesByUserId(userId, now)
        for (userRole in userRoles) {
            val role = userRole.role

            // Add direct role permissions
            permissions.addAll(role!!.permissions)

            // Add permission group permissions
            for (permissionGroup in role.permissionGroups) {
                permissions.addAll(permissionGroup.permissions)
            }
        }

        return permissions.toList()
    }

    /**
     * Get all roles for a user
     */
    fun getUserRoles(userId: UUID): List<Role> {
        val now = Instant.now()
        return userRoleRepository.findActiveRolesByUserId(userId, now)
            .map { it.role!! }
    }

    /**
     * Get permission audit trail for a user
     */
    fun getPermissionAuditTrail(userId: UUID): List<PermissionAuditLog> {
        return permissionAuditLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    /**
     * Clean up expired permissions and roles
     */
    @Transactional
    fun cleanupExpiredPermissions() {
        val now = Instant.now()
        logger.info("Cleaning up expired permissions and roles at: $now")

        // Find and deactivate expired user permissions
        val expiredPermissions = userPermissionRepository.findAll()
            .filter { it.expiresAt != null && it.expiresAt!!.isBefore(now) && it.isActive }

        expiredPermissions.forEach { userPermission ->
            userPermission.isActive = false
            userPermissionRepository.save(userPermission)

            logPermissionChange(
                user = userPermission.user!!,
                action = "expired",
                permissionType = "direct_permission",
                permissionIdentifier = userPermission.permission!!.name!!
            )
        }

        // Find and deactivate expired user roles
        val expiredRoles = userRoleRepository.findAll()
            .filter { it.expiresAt != null && it.expiresAt!!.isBefore(now) && it.isActive }

        expiredRoles.forEach { userRole ->
            userRole.isActive = false
            userRoleRepository.save(userRole)

            logPermissionChange(
                user = userRole.user!!,
                action = "expired",
                permissionType = "role",
                permissionIdentifier = userRole.role!!.name!!
            )
        }

        logger.info("Cleanup completed: ${expiredPermissions.size} permissions and ${expiredRoles.size} roles expired")
    }

    /**
     * Migrate existing user roles to new permission system
     * This method handles the transition from old string-based roles to new permission system
     */
    @Transactional
    fun migrateUserToNewPermissionSystem(user: User): UserRole? {
        if (user.userRole == null) {
            logger.warn("User ${user.id} has no role to migrate")
            return null
        }

        // Find the corresponding role in the new system
        val roleOptional = roleRepository.findByName(user.userRole!!)
        if (!roleOptional.isPresent) {
            logger.error("No role found for migration: ${user.userRole}")
            return null
        }

        val role = roleOptional.get()

        // Check if user already has this role assigned
        val existingRole = userRoleRepository.findByUserIdAndRoleId(user.id!!, role.id!!)
        if (existingRole.isPresent && existingRole.get().isValid()) {
            logger.info("User ${user.id} already has role ${role.name} assigned")
            return existingRole.get()
        }

        // Create new role assignment (using system migration)
        val userRole = UserRole(
            id = UserRoleId(user.id!!, role.id!!),
            user = user,
            role = role,
            assignedBy = user, // Self-assigned during migration
            assignedAt = Instant.now()
        )

        val savedRole = userRoleRepository.save(userRole)

        // Log the migration
        logPermissionChange(
            user = user,
            action = "migrated",
            permissionType = "role",
            permissionIdentifier = role.name!!,
            contextData = mapOf("migration" to true, "original_role" to user.userRole!!)
        )

        logger.info("Successfully migrated user ${user.id} from role ${user.userRole} to new permission system")
        return savedRole
    }

    /**
     * Batch migrate all users to new permission system
     */
    @Transactional
    fun migrateAllUsersToNewPermissionSystem() {
        logger.info("Starting batch migration of all users to new permission system")

        val users = userRepository.findAll()
        var successCount = 0
        var errorCount = 0

        for (user in users) {
            try {
                migrateUserToNewPermissionSystem(user)
                successCount++
            } catch (e: Exception) {
                logger.error("Failed to migrate user ${user.id}: ${e.message}", e)
                errorCount++
            }
        }

        logger.info("Migration completed: $successCount successful, $errorCount failed")
    }

    /**
     * Evaluate context conditions for permission grants
     */
    private fun evaluateContext(
        permissionContext: Map<String, Any>?,
        requestContext: Map<String, Any>?
    ): Boolean {
        // If no context restrictions, allow access
        if (permissionContext.isNullOrEmpty()) {
            return true
        }

        // If context is required but not provided, deny access
        if (requestContext.isNullOrEmpty()) {
            return false
        }

        // Evaluate context conditions
        // Example: Check if user can only approve up to certain amount
        permissionContext["max_amount"]?.let { maxAmount ->
            requestContext["amount"]?.let { requestAmount ->
                if ((requestAmount as Number).toDouble() > (maxAmount as Number).toDouble()) {
                    return false
                }
            }
        }

        // Example: Check if permission is limited to specific departments
        permissionContext["departments"]?.let { allowedDepts ->
            requestContext["department"]?.let { requestDept ->
                if (allowedDepts is List<*> && !allowedDepts.contains(requestDept)) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Log permission changes for audit purposes
     */
    private fun logPermissionChange(
        user: User,
        action: String,
        permissionType: String,
        permissionIdentifier: String,
        grantedBy: User? = null,
        contextData: Map<String, Any>? = null
    ) {
        val auditLog = PermissionAuditLog(
            user = user,
            action = action,
            permissionType = permissionType,
            permissionIdentifier = permissionIdentifier,
            grantedBy = grantedBy,
            contextData = contextData
        )

        permissionAuditLogRepository.save(auditLog)
        logger.info("Logged permission change: $action $permissionType $permissionIdentifier for user ${user.id}")
    }

    /**
     * Get comprehensive user permissions as DTOs (safe for JSON serialization)
     */
    fun getUserPermissionsDTO(userId: UUID): UserPermissionsDTO {
        val now = Instant.now()

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        // Get user roles with details
        val userRoles = userRoleRepository.findActiveRolesByUserId(userId, now)
        val userRoleDTOs = userRoles.map { userRole ->
            UserRoleDTO(
                roleId = userRole.role!!.id!!,
                roleName = userRole.role.name!!,
                roleDescription = userRole.role.description,
                assignedBy = userRole.assignedBy!!.id!!,
                assignedByUsername = userRole.assignedBy.username,
                assignedAt = userRole.assignedAt,
                expiresAt = userRole.expiresAt,
                isActive = userRole.isActive,
                isValid = userRole.isValid()
            )
        }

        // Get direct permissions
        val directPermissions = userPermissionRepository.findActivePermissionsByUserId(userId, now)
        val directPermissionDTOs = directPermissions.map { userPermission ->
            UserPermissionDTO(
                permissionId = userPermission.permission!!.id!!,
                permissionName = userPermission.permission.name!!,
                permissionDescription = userPermission.permission.description,
                resource = userPermission.permission.resource!!,
                action = userPermission.permission.action!!,
                grantedBy = userPermission.grantedBy!!.id!!,
                grantedByUsername = userPermission.grantedBy.username,
                grantedAt = userPermission.grantedAt,
                expiresAt = userPermission.expiresAt,
                isActive = userPermission.isActive,
                isValid = userPermission.isValid(),
                contextData = userPermission.contextData
            )
        }

        // Get all unique permissions
        val allPermissions = getUserPermissions(userId)
        val allPermissionDTOs = allPermissions.map { permission ->
            PermissionDTO(
                id = permission.id!!,
                name = permission.name!!,
                description = permission.description,
                resource = permission.resource!!,
                action = permission.action!!,
                createdAt = permission.createdAt
            )
        }

        // Create summary
        val activeRoles = userRoleDTOs.count { it.isValid }
        val activeDirectPermissions = directPermissionDTOs.count { it.isValid }
        val hasExpiring = directPermissionDTOs.any {
            it.expiresAt != null && it.expiresAt.isBefore(now.plus(Duration.ofDays(7)))
        }

        val summary = PermissionSummaryDTO(
            totalRoles = userRoleDTOs.size,
            activeRoles = activeRoles,
            totalDirectPermissions = directPermissionDTOs.size,
            activeDirectPermissions = activeDirectPermissions,
            totalUniquePermissions = allPermissionDTOs.size,
            hasExpiring = hasExpiring,
            highestRole = getHighestRoleLevel(userId)
        )

        return UserPermissionsDTO(
            userId = userId,
            username = user.username,
            roles = userRoleDTOs,
            directPermissions = directPermissionDTOs,
            allPermissions = allPermissionDTOs,
            permissionSummary = summary
        )
    }

    /**
     * Get permission audit trail as DTOs
     */
    fun getPermissionAuditTrailDTO(userId: UUID): List<PermissionAuditLogDTO> {
        val auditLogs = permissionAuditLogRepository.findByUserIdOrderByCreatedAtDesc(userId)

        return auditLogs.map { log ->
            PermissionAuditLogDTO(
                id = log.id!!,
                userId = log.user!!.id!!,
                username = log.user.username,
                action = log.action!!,
                permissionType = log.permissionType!!,
                permissionIdentifier = log.permissionIdentifier!!,
                grantedBy = log.grantedBy?.id,
                grantedByUsername = log.grantedBy?.username,
                contextData = log.contextData,
                createdAt = log.createdAt
            )
        }
    }

    /**
     * Get the highest role level for a user (for display purposes)
     */
    private fun getHighestRoleLevel(userId: UUID): String {
        val roles = getUserRoles(userId)
        val roleNames = roles.map { it.name }

        return when {
            "Admin" in roleNames -> "Admin"
            "Manager" in roleNames -> "Manager"
            "Service Advisor" in roleNames -> "Service Advisor"
            "Technician" in roleNames -> "Technician"
            "Stores" in roleNames -> "Stores"
            "Client" in roleNames -> "Client"
            else -> "Unknown"
        }
    }
}