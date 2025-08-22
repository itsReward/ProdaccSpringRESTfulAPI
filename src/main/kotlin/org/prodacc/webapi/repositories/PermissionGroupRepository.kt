package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.PermissionGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PermissionGroupRepository : JpaRepository<PermissionGroup, UUID> {

    fun findByName(name: String): Optional<PermissionGroup>

    @Query("SELECT pg FROM PermissionGroup pg JOIN FETCH pg.permissions WHERE pg.id = :id")
    fun findByIdWithPermissions(@Param("id") id: UUID): Optional<PermissionGroup>

    @Query("SELECT pg FROM PermissionGroup pg JOIN FETCH pg.permissions WHERE pg.name = :name")
    fun findByNameWithPermissions(@Param("name") name: String): Optional<PermissionGroup>
}