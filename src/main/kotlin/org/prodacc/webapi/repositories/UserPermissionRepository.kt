package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.UserPermission
import org.prodacc.webapi.models.UserPermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional
import java.util.UUID

@Repository
interface UserPermissionRepository : CrudRepository<UserPermission, UserPermissionId> {

    @Query("""
        SELECT up FROM UserPermission up 
        JOIN FETCH up.permission p 
        WHERE up.user.id = :userId 
        AND up.isActive = true 
        AND (up.expiresAt IS NULL OR up.expiresAt > :now)
    """)
    fun findActivePermissionsByUserId(@Param("userId") userId: UUID, @Param("now") now: Instant): List<UserPermission>

    @Query("""
        SELECT up FROM UserPermission up 
        WHERE up.user.id = :userId 
        AND up.isActive = true
    """)
    fun findByUserId(@Param("userId") userId: UUID): List<UserPermission>

    @Query("""
        SELECT up FROM UserPermission up 
        WHERE up.permission.resource = :resource 
        AND up.permission.action = :action 
        AND up.user.id = :userId 
        AND up.isActive = true 
        AND (up.expiresAt IS NULL OR up.expiresAt > :now)
    """)
    fun findActiveUserPermission(
        @Param("userId") userId: UUID,
        @Param("resource") resource: String,
        @Param("action") action: String,
        @Param("now") now: Instant
    ): Optional<UserPermission>

    fun findByUserIdAndPermissionId(userId: UUID, permissionId: UUID): Optional<UserPermission>
}