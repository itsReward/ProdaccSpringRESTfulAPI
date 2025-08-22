package org.prodacc.webapi.services.dataTransferObjects.permission

/**
 * Data Transfer Object for Permission Context
 */
data class PermissionContext(
    val resource: String,
    val action: String,
    val contextData: Map<String, Any>? = null
)

