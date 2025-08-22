package org.prodacc.webapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.Instant
import java.util.*

/**
 * User Role Assignment Entity
 * Links users to roles with temporal and audit capabilities
 */
@Entity
@Table(name = "user_roles")
data class UserRole(
    @EmbeddedId
    val id: UserRoleId? = null,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    val role: Role? = null,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    val assignedBy: User? = null,

    @Column(name = "assigned_at", nullable = false)
    val assignedAt: Instant = Instant.now(),

    @Column(name = "expires_at")
    val expiresAt: Instant? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {
    /**
     * Check if this role assignment is currently valid
     */
    fun isValid(): Boolean {
        val now = Instant.now()
        return isActive && (expiresAt == null || expiresAt.isAfter(now))
    }
}

/**
 * Composite Primary Key for UserRole
 */
@Embeddable
data class UserRoleId(
    @Column(name = "user_id")
    val userId: UUID? = null,

    @Column(name = "role_id")
    val roleId: UUID? = null
) : java.io.Serializable
