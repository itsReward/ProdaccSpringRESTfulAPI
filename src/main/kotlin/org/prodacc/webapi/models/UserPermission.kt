package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

/**
 * Direct User Permission Entity
 * Allows direct permission grants to users outside of roles
 */
@Entity
@Table(name = "user_permissions")
data class UserPermission(
    @EmbeddedId
    val id: UserPermissionId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    val permission: Permission? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false)
    val grantedBy: User? = null,

    @Column(name = "granted_at", nullable = false)
    val grantedAt: Instant = Instant.now(),

    @Column(name = "expires_at")
    val expiresAt: Instant? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data", columnDefinition = "jsonb")
    val contextData: Map<String, Any>? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {
    /**
     * Check if this permission grant is currently valid
     */
    fun isValid(): Boolean {
        val now = Instant.now()
        return isActive && (expiresAt == null || expiresAt.isAfter(now))
    }
}

/**
 * Composite Primary Key for UserPermission
 */
@Embeddable
data class UserPermissionId(
    @Column(name = "user_id")
    val userId: UUID? = null,

    @Column(name = "permission_id")
    val permissionId: UUID? = null
) : java.io.Serializable
