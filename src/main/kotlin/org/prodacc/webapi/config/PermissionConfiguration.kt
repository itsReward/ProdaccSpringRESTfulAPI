package org.prodacc.webapi.config

import org.prodacc.webapi.services.PermissionService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.UUID

/**
 * Configuration for Permission System
 */
@Configuration
@EnableScheduling
class PermissionConfiguration {

    /**
     * Initialize permission system on startup
     */
    @Bean
    fun permissionSystemInitializer(permissionService: PermissionService): CommandLineRunner {
        return CommandLineRunner {
            try {
                // Migrate all users to new permission system on startup
                permissionService.migrateAllUsersToNewPermissionSystem()
                println("✅ Permission system migration completed successfully")
            } catch (e: Exception) {
                println("❌ Permission system migration failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Scheduled task to clean up expired permissions
     * Runs every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    fun scheduledPermissionCleanup(permissionService: PermissionService) {
        try {
            permissionService.cleanupExpiredPermissions()
            println("✅ Scheduled permission cleanup completed")
        } catch (e: Exception) {
            println("❌ Scheduled permission cleanup failed: ${e.message}")
            e.printStackTrace()
        }
    }
}

/**
 * Permission Constants for easy reference
 */
object PermissionConstants {

    // Resources
    object Resources {
        const val USER = "user"
        const val EMPLOYEE = "employee"
        const val CLIENT = "client"
        const val VEHICLE = "vehicle"
        const val JOBCARD = "jobcard"
        const val TIMESHEET = "timesheet"
        const val INVENTORY = "inventory"
        const val PARTS = "parts"
        const val INVOICE = "invoice"
        const val QUOTATION = "quotation"
        const val APPOINTMENT = "appointment"
        const val PROFILE = "profile"
        const val SYSTEM = "system"
        const val PERMISSIONS = "permissions"
    }

    // Actions
    object Actions {
        const val CREATE = "create"
        const val READ = "read"
        const val UPDATE = "update"
        const val DELETE = "delete"
        const val READ_ALL = "read.all"
        const val READ_OWN = "read.own"
        const val READ_ASSIGNED = "read.assigned"
        const val REQUEST = "request"
        const val APPROVE = "approve"
        const val CONFIGURE = "configure"
        const val GRANT = "grant"
        const val REVOKE = "revoke"
        const val UPDATE_OWN = "update.own"
        const val READ_OWN_PROFILE = "read.own"
    }

    // Permission Names (resource.action format)
    object Permissions {
        // User Management
        const val USER_CREATE = "user.create"
        const val USER_READ = "user.read"
        const val USER_UPDATE = "user.update"
        const val USER_DELETE = "user.delete"

        // Job Card Management
        const val JOBCARD_CREATE = "jobcard.create"
        const val JOBCARD_READ_ALL = "jobcard.read.all"
        const val JOBCARD_READ_OWN = "jobcard.read.own"
        const val JOBCARD_READ_ASSIGNED = "jobcard.read.assigned"
        const val JOBCARD_UPDATE = "jobcard.update"
        const val JOBCARD_DELETE = "jobcard.delete"

        // Parts Management
        const val PARTS_REQUEST = "parts.request"
        const val PARTS_APPROVE = "parts.approve"

        // System Administration
        const val SYSTEM_CONFIGURE = "system.configure"
        const val PERMISSIONS_GRANT = "permissions.grant"
        const val PERMISSIONS_REVOKE = "permissions.revoke"

        // Profile Management
        const val PROFILE_READ_OWN = "profile.read.own"
        const val PROFILE_UPDATE_OWN = "profile.update.own"
    }

    // Role Names
    object Roles {
        const val ADMIN = "Admin"
        const val MANAGER = "Manager"
        const val SERVICE_ADVISOR = "Service Advisor"
        const val TECHNICIAN = "Technician"
        const val STORES = "Stores"
        const val CLIENT = "Client"
    }

    // Permission Groups
    object PermissionGroups {
        const val USER_MANAGEMENT = "user_management"
        const val EMPLOYEE_MANAGEMENT = "employee_management"
        const val CLIENT_MANAGEMENT = "client_management"
        const val JOBCARD_FULL_ACCESS = "jobcard_full_access"
        const val JOBCARD_OWN_ACCESS = "jobcard_own_access"
        const val JOBCARD_ASSIGNED_ACCESS = "jobcard_assigned_access"
        const val INVENTORY_MANAGEMENT = "inventory_management"
        const val PARTS_OPERATIONS = "parts_operations"
        const val FINANCIAL_OPERATIONS = "financial_operations"
        const val BASIC_PROFILE = "basic_profile"
        const val SYSTEM_ADMINISTRATION = "system_administration"
    }
}

/**
 * Utility class for common permission checks
 */
@org.springframework.stereotype.Component
class PermissionUtils(
    private val permissionService: PermissionService
) {

    /**
     * Check if user is admin
     */
    fun isAdmin(userId: UUID): Boolean {
        val roles = permissionService.getUserRoles(userId)
        return roles.any { it.name == PermissionConstants.Roles.ADMIN }
    }

    /**
     * Check if user is manager or admin
     */
    fun isManagerOrAdmin(userId: UUID): Boolean {
        val roles = permissionService.getUserRoles(userId)
        return roles.any { it.name in listOf(PermissionConstants.Roles.ADMIN, PermissionConstants.Roles.MANAGER) }
    }

    /**
     * Check if user can manage other users
     */
    fun canManageUsers(userId: UUID): Boolean {
        return permissionService.hasPermission(userId, PermissionConstants.Resources.USER, PermissionConstants.Actions.CREATE) ||
                permissionService.hasPermission(userId, PermissionConstants.Resources.USER, PermissionConstants.Actions.UPDATE) ||
                permissionService.hasPermission(userId, PermissionConstants.Resources.USER, PermissionConstants.Actions.DELETE)
    }

    /**
     * Check if user can grant permissions
     */
    fun canGrantPermissions(userId: UUID): Boolean {
        return permissionService.hasPermission(userId, PermissionConstants.Resources.PERMISSIONS, PermissionConstants.Actions.GRANT)
    }

    /**
     * Get the highest role level for a user (for display purposes)
     */
    fun getHighestRoleLevel(userId: UUID): String {
        val roles = permissionService.getUserRoles(userId)
        val roleNames = roles.map { it.name }

        return when {
            PermissionConstants.Roles.ADMIN in roleNames -> PermissionConstants.Roles.ADMIN
            PermissionConstants.Roles.MANAGER in roleNames -> PermissionConstants.Roles.MANAGER
            PermissionConstants.Roles.SERVICE_ADVISOR in roleNames -> PermissionConstants.Roles.SERVICE_ADVISOR
            PermissionConstants.Roles.TECHNICIAN in roleNames -> PermissionConstants.Roles.TECHNICIAN
            PermissionConstants.Roles.STORES in roleNames -> PermissionConstants.Roles.STORES
            PermissionConstants.Roles.CLIENT in roleNames -> PermissionConstants.Roles.CLIENT
            else -> "Unknown"
        }
    }

    /**
     * Check if user has temporary permissions that will expire soon
     */
    fun hasExpiringPermissions(userId: UUID, withinHours: Long = 24): Boolean {
        // This would require additional repository methods to check expiring permissions
        // Implementation depends on your specific needs
        return false // Placeholder
    }

    /**
     * Get summary of user's permission sources
     */
    fun getPermissionSummary(userId: UUID): PermissionSummary {
        val roles = permissionService.getUserRoles(userId)
        val allPermissions = permissionService.getUserPermissions(userId)

        // Count permissions from roles vs direct grants
        // This is a simplified implementation
        val rolePermissionCount = roles.sumOf { role ->
            role.permissions.size + role.permissionGroups.sumOf { it.permissions.size }
        }

        val directPermissionCount = allPermissions.size - rolePermissionCount

        return PermissionSummary(
            userId = userId,
            roleCount = roles.size,
            roleNames = roles.map { it.name!! },
            totalPermissions = allPermissions.size,
            roleBasedPermissions = rolePermissionCount,
            directPermissions = directPermissionCount.coerceAtLeast(0),
            highestRole = getHighestRoleLevel(userId)
        )
    }
}

/**
 * Data class for permission summary
 */
data class PermissionSummary(
    val userId: UUID,
    val roleCount: Int,
    val roleNames: List<String>,
    val totalPermissions: Int,
    val roleBasedPermissions: Int,
    val directPermissions: Int,
    val highestRole: String
)

/**
 * Exception classes for permission-related errors
 */
class PermissionDeniedException(message: String) : SecurityException(message)

class PermissionExpiredException(message: String) : SecurityException(message)

class PermissionConflictException(message: String) : IllegalStateException(message)