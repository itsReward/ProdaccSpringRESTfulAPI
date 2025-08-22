package org.prodacc.webapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

/**
 * Core Permission Entity
 * Represents granular permissions in the system
 */
@Entity
@Table(name = "permissions")
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    val name: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false, length = 50)
    val resource: String? = null,

    @Column(nullable = false, length = 50)
    val action: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    val permissionGroups: Set<PermissionGroup> = mutableSetOf()

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    val roles: Set<Role> = mutableSetOf()

    @JsonIgnore
    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL])
    val userPermissions: Set<UserPermission> = mutableSetOf()
}