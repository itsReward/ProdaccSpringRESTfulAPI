package org.prodacc.webapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.Instant
import java.util.*

/**
 * Permission Group Entity
 * Groups related permissions together for easier management
 */
@Entity
@Table(name = "permission_groups")
data class PermissionGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    val name: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
) {
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "permission_group_permissions",
        joinColumns = [JoinColumn(name = "permission_group_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    val permissions: Set<Permission> = mutableSetOf()

    @JsonIgnore
    @ManyToMany(mappedBy = "permissionGroups")
    val roles: Set<Role> = mutableSetOf()
}