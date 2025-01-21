package org.prodacc.webapi.services.synchronisation

import org.springframework.stereotype.Service

@Service
class ConflictResolutionService {
    fun resolveConflict(localEntity: Any, remoteEntity: Any): Any {
        // Implement your conflict resolution strategy here
        // For example, you could use a "last write wins" strategy:
        return if ((localEntity as VersionedEntity).version > (remoteEntity as VersionedEntity).version) {
            localEntity
        } else {
            remoteEntity
        }
    }
}

interface VersionedEntity {
    val version: Long
}