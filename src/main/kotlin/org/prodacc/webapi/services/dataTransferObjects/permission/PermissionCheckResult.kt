package org.prodacc.webapi.services.dataTransferObjects.permission

/**
 * Permission Check Result
 */
data class PermissionCheckResult(
    val hasPermission: Boolean,
    val source: String, // "role" or "direct"
    val roleName: String? = null,
    val permissionName: String? = null,
    val context: Map<String, Any>? = null
)