package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.ClientService
import org.prodacc.webapi.services.dataTransferObjects.NewClient
import org.prodacc.webapi.services.dataTransferObjects.ResponseClientWithVehicles
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/clients")
class ClientController(
    private val clientService: ClientService,
) {

    @GetMapping("/all")
    fun getAll(): Iterable<ResponseClientWithVehicles> = clientService.getAll()


    @GetMapping("/get/{id}")
    fun getClientById(@PathVariable id: UUID): ResponseClientWithVehicles = clientService.getClientById(id)


    @PostMapping("/new")
    fun createClient(@RequestBody client: NewClient): ResponseClientWithVehicles =
        clientService.createClient(client)


    @PutMapping("/update/{id}")
    fun updateClient(@RequestBody updatedClient: NewClient, @PathVariable id: UUID): ResponseClientWithVehicles =
        clientService.updateClient(updatedClient, id )

    @DeleteMapping("/delete/{id}")
    fun deleteClientById(@PathVariable id: UUID): ResponseEntity<String> = clientService.deleteClientById(id)


}