package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface RoleRepository : CrudRepository<Role, UUID> {

    fun findByName(name: String): Optional<Role>

    fun findByIsSystemRole(isSystemRole: Boolean): List<Role>

    @Query("""
        SELECT r FROM Role r 
        LEFT JOIN FETCH r.permissionGroups pg 
        LEFT JOIN FETCH pg.permissions 
        LEFT JOIN FETCH r.permissions 
        WHERE r.id = :id
    """)
    fun findByIdWithPermissions(@Param("id") id: UUID): Optional<Role>

    @Query("""
        SELECT r FROM Role r 
        LEFT JOIN FETCH r.permissionGroups pg 
        LEFT JOIN FETCH pg.permissions 
        LEFT JOIN FETCH r.permissions 
        WHERE r.name = :name
    """)
    fun findByNameWithPermissions(@Param("name") name: String): Optional<Role>
}