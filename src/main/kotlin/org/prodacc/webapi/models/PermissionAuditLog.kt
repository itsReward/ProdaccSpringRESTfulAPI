package org.prodacc.webapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID


/**
 * Permission Audit Log Entity
 * Tracks all permission changes for audit purposes
 */
@Entity
@Table(name = "permission_audit_log")
data class PermissionAuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @Column(nullable = false, length = 50)
    val action: String? = null, // granted, revoked, expired

    @Column(name = "permission_type", nullable = false, length = 50)
    val permissionType: String? = null, // role, direct_permission

    @Column(name = "permission_identifier", nullable = false)
    val permissionIdentifier: String? = null, // role name or permission name

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    val grantedBy: User? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data", columnDefinition = "jsonb")
    val contextData: Map<String, Any>? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
)

