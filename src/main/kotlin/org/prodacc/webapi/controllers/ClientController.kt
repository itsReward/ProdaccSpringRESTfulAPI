package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Client
import org.prodacc.webapi.models.dataTransferObjects.ClientWithVehiclesNameAndId
import org.prodacc.webapi.models.dataTransferObjects.UpdateClient
import org.prodacc.webapi.models.dataTransferObjects.VehicleWithIdAndName
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.Throws


@RestController
@RequestMapping("api/v1/clients")
class ClientController(
    val repository: ClientRepository,
    private val vehicleRepository: VehicleRepository
) {

    @GetMapping("/all")
    fun getAll(): Iterable<ClientWithVehiclesNameAndId> {
        return repository.findAll().map { client ->

            val vehiclesWithIdAndNameList = client.vehicles.map { vehicle ->
                vehicleRepository.findById(vehicle.id!!).map {
                        VehicleWithIdAndName(
                            id = it.id,
                            make = it.make,
                            model = it.model
                        )
                }.orElseThrow { RuntimeException("Vehicle not found") }
            }

            ClientWithVehiclesNameAndId(
                id = client.id,
                clientName = client.clientName,
                clientSurname = client.clientSurname,
                gender = client.gender,
                jobTitle = client.jobTitle,
                company = client.company,
                phone = client.phone,
                email = client.email,
                address = client.address,
                vehicles = vehiclesWithIdAndNameList
            )
        }
    }


    @GetMapping("/get/{id}")
    fun getClientById(@PathVariable id: UUID): Optional<Client> = repository.findById(id)


    @PostMapping("/new")
    fun createClient(@RequestBody client: Client): Client =  repository.save(client)


    @PutMapping("/update/{id}")
    fun updateClient(@RequestBody updatedClient: UpdateClient, @PathVariable id: UUID): ResponseEntity<Client> {
        return repository.findById(id).map { existingClient ->
            val updated = existingClient.copy(
                clientName = updatedClient.clientName?: existingClient.clientName,
                clientSurname = updatedClient.clientSurname?: existingClient.clientSurname,
                gender = updatedClient.gender?: existingClient.gender,
                jobTitle = updatedClient.jobTitle?:existingClient.jobTitle,
                company = updatedClient.company?: existingClient.company,
                phone = updatedClient.phone?: existingClient.phone,
                email = updatedClient.email?: existingClient.email,
                address = updatedClient.address?: existingClient.address
            )
            ResponseEntity.ok().body(repository.save(updated))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/delete/{id}")
    fun deleteClientById(@PathVariable id: UUID): ResponseEntity<String> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity("Client deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("Client not found", HttpStatus.NOT_FOUND)
        }
    }


}