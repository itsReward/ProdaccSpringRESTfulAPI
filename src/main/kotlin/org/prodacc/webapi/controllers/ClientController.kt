package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.dataTransferObjects.ClientWithVehiclesNameAndId
import org.prodacc.webapi.models.dataTransferObjects.NewClient
import org.prodacc.webapi.services.ClientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/v1/clients")
class ClientController(
    private val clientService: ClientService,
) {

    @GetMapping("/all")
    fun getAll(): Iterable<ClientWithVehiclesNameAndId> = clientService.getAll()


    @GetMapping("/get/{id}")
    fun getClientById(@PathVariable id: UUID): ClientWithVehiclesNameAndId = clientService.getClientById(id)


    @PostMapping("/new")
    fun createClient(@RequestBody client: NewClient): ClientWithVehiclesNameAndId =
        clientService.createClient(client)


    @PutMapping("/update/{id}")
    fun updateClient(@RequestBody updatedClient: NewClient, @PathVariable id: UUID): ClientWithVehiclesNameAndId =
        clientService.updateClient(updatedClient, id )

    @DeleteMapping("/delete/{id}")
    fun deleteClientById(@PathVariable id: UUID): ResponseEntity<String> = clientService.deleteClientById(id)


}