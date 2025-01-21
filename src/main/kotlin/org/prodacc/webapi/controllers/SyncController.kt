package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.synchronisation.SyncData
import org.prodacc.webapi.services.synchronisation.SyncService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sync")
class SyncController(private val syncService: SyncService) {

    @PostMapping("/entity")
    fun syncEntity(@RequestBody syncData: SyncData) {
        syncService.syncFromOffline(syncData)
    }
}