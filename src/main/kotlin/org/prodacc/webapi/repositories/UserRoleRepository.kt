package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.UserRole
import org.prodacc.webapi.models.UserRoleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional
import java.util.UUID

@Repository
interface UserRoleRepository : JpaRepository<UserRole, UserRoleId> {

    @Query("""
        SELECT ur FROM UserRole ur 
        JOIN FETCH ur.role r 
        LEFT JOIN FETCH r.permissionGroups pg 
        LEFT JOIN FETCH pg.permissions 
        LEFT JOIN FETCH r.permissions 
        WHERE ur.user.id = :userId 
        AND ur.isActive = true 
        AND (ur.expiresAt IS NULL OR ur.expiresAt > :now)
    """)
    fun findActiveRolesByUserId(@Param("userId") userId: UUID, @Param("now") now: Instant): List<UserRole>

    @Query("""
        SELECT ur FROM UserRole ur 
        WHERE ur.user.id = :userId 
        AND ur.isActive = true
    """)
    fun findByUserId(@Param("userId") userId: UUID): List<UserRole>

    @Query("""
        SELECT ur FROM UserRole ur 
        WHERE ur.role.id = :roleId 
        AND ur.isActive = true
    """)
    fun findByRoleId(@Param("roleId") roleId: UUID): List<UserRole>

    fun findByUserIdAndRoleId(userId: UUID, roleId: UUID): Optional<UserRole>
}