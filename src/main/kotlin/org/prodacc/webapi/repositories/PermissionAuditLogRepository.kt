package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.PermissionAuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface PermissionAuditLogRepository : CrudRepository<PermissionAuditLog, UUID> {

    @Query("""
        SELECT pal FROM PermissionAuditLog pal 
        WHERE pal.user.id = :userId 
        ORDER BY pal.createdAt DESC
    """)
    fun findByUserIdOrderByCreatedAtDesc(@Param("userId") userId: UUID): List<PermissionAuditLog>

    @Query("""
        SELECT pal FROM PermissionAuditLog pal 
        WHERE pal.grantedBy.id = :grantedById 
        ORDER BY pal.createdAt DESC
    """)
    fun findByGrantedByIdOrderByCreatedAtDesc(@Param("grantedById") grantedById: UUID): List<PermissionAuditLog>

    @Query("""
        SELECT pal FROM PermissionAuditLog pal 
        WHERE pal.action = :action 
        AND pal.createdAt BETWEEN :startDate AND :endDate 
        ORDER BY pal.createdAt DESC
    """)
    fun findByActionAndDateRange(
        @Param("action") action: String,
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<PermissionAuditLog>
}