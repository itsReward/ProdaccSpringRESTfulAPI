package org.prodacc.webapi.services.dataTransferObjects

import java.time.Instant
import java.util.*

/**
 * Data Transfer Objects for Permission System
 * These DTOs prevent infinite recursion when serializing to JSON
 */

/**
 * Permission DTO without circular references
 */
data class PermissionDTO(
    val id: UUID,
    val name: String,
    val description: String?,
    val resource: String,
    val action: String,
    val createdAt: Instant
)

/**
 * Permission Group DTO without circular references
 */
data class PermissionGroupDTO(
    val id: UUID,
    val name: String,
    val description: String?,
    val permissions: List<PermissionDTO>,
    val createdAt: Instant
)

/**
 * Role DTO without circular references
 */
data class RoleDTO(
    val id: UUID,
    val name: String,
    val description: String?,
    val isSystemRole: Boolean,
    val permissionGroups: List<PermissionGroupDTO>,
    val directPermissions: List<PermissionDTO>,
    val createdAt: Instant
)

/**
 * User Role Assignment DTO
 */
data class UserRoleDTO(
    val roleId: UUID,
    val roleName: String,
    val roleDescription: String?,
    val assignedBy: UUID,
    val assignedByUsername: String?,
    val assignedAt: Instant,
    val expiresAt: Instant?,
    val isActive: Boolean,
    val isValid: Boolean
)

/**
 * User Permission Assignment DTO
 */
data class UserPermissionDTO(
    val permissionId: UUID,
    val permissionName: String,
    val permissionDescription: String?,
    val resource: String,
    val action: String,
    val grantedBy: UUID,
    val grantedByUsername: String?,
    val grantedAt: Instant,
    val expiresAt: Instant?,
    val isActive: Boolean,
    val isValid: Boolean,
    val contextData: Map<String, Any>?
)

/**
 * Comprehensive User Permissions Response
 */
data class UserPermissionsResponse(
    val userId: UUID,
    val username: String?,
    val roles: List<UserRoleDTO>,
    val directPermissions: List<UserPermissionDTO>,
    val allPermissions: List<PermissionDTO>,
    val permissionSummary: PermissionSummaryDTO
)

/**
 * Alias for backward compatibility
 */
typealias UserPermissionsDTO = UserPermissionsResponse

/**
 * Permission Summary DTO
 */
data class PermissionSummaryDTO(
    val totalRoles: Int,
    val activeRoles: Int,
    val totalDirectPermissions: Int,
    val activeDirectPermissions: Int,
    val totalUniquePermissions: Int,
    val hasExpiring: Boolean,
    val highestRole: String
)

/**
 * Permission Audit Log DTO
 */
data class PermissionAuditLogDTO(
    val id: UUID,
    val userId: UUID,
    val username: String?,
    val action: String,
    val permissionType: String,
    val permissionIdentifier: String,
    val grantedBy: UUID?,
    val grantedByUsername: String?,
    val contextData: Map<String, Any>?,
    val createdAt: Instant
)