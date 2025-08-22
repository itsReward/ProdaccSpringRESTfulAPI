package org.prodacc.webapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import java.util.UUID

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*
/**
 * Role Entity
 * Represents collections of permission groups and individual permissions
 */
@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    val name: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "is_system_role", nullable = false)
    val isSystemRole: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permission_groups",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_group_id")]
    )
    val permissionGroups: Set<PermissionGroup> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    val permissions: Set<Permission> = mutableSetOf()

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL])
    val userRoles: Set<UserRole> = mutableSetOf()
}