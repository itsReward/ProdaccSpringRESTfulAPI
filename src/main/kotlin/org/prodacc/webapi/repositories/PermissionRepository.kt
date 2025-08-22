package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PermissionRepository : CrudRepository<Permission, UUID> {

    fun findByName(name: String): Optional<Permission>

    fun findByResource(resource: String): List<Permission>

    fun findByResourceAndAction(resource: String, action: String): Optional<Permission>

    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.action IN :actions")
    fun findByResourceAndActions(
        @Param("resource") resource: String,
        @Param("actions") actions: List<String>
    ): List<Permission>
}

