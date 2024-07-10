package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Client
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.NewClient
import org.prodacc.webapi.services.dataTransferObjects.ResponseClientWithVehicles
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ClientService (
    private val clientRepository: ClientRepository,
    private val vehicleRepository: VehicleRepository
)
{
    private val logger = LoggerFactory.getLogger(ClientService::class.java)

    fun getAll(): Iterable<ResponseClientWithVehicles> {
        logger.info("Getting all clients")
        return clientRepository.findAll().map { it.toClientWithVehicleNameAndId()
        }
    }

    fun getClientById( id: UUID): ResponseClientWithVehicles {
        logger.info("Getting client with id: $id")
        return clientRepository.findById(id)
            .map { it.toClientWithVehicleNameAndId() }
            .orElseThrow { EntityNotFoundException("Client with id: $id not found") }
    }


    @Transactional
    fun createClient( client: NewClient): ResponseClientWithVehicles {
        logger.info("Creating new client")
        val newClient = try {
            Client(
                clientName = client.clientName!!,
                clientSurname = client.clientSurname!!,
                gender = client.gender!!,
                jobTitle = client.jobTitle,
                company = client.company,
                phone = client.phone!!,
                email = client.email,
                address = client.address
            )
        } catch (e: Exception) {
            throw NullPointerException("client name, surname, gender and phone cannot be null!")
        }
        return clientRepository.save( newClient ).toClientWithVehicleNameAndId()
    }

    @Transactional
    fun updateClient(updatedClient: NewClient, id: UUID): ResponseClientWithVehicles {
        logger.info("Updating client with id: $id")
        val existingClient = clientRepository.findById(id).orElseThrow { EntityNotFoundException("Client with id: $id not found") }
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
        return clientRepository.save(updated).toClientWithVehicleNameAndId()
    }

    @Transactional
    fun deleteClientById(id: UUID): ResponseEntity<String> {
        logger.info("Deleting client with id: $id")
        return if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id)
            ResponseEntity("Client deleted successfully", HttpStatus.OK)
        } else {
             throw EntityNotFoundException("Client with id: $id not found")
        }
    }


    private fun Client.toClientWithVehicleNameAndId(): ResponseClientWithVehicles {
        val vehiclesWithIdAndNameList = this.vehicles.map { vehicle ->
            vehicleRepository.findById(vehicle.id!!).map {
                org.prodacc.webapi.services.dataTransferObjects.VehicleWithIdAndName(
                    id = it.id,
                    make = it.make,
                    model = it.model
                )
            }.orElseThrow { EntityNotFoundException("Vehicle with id: ${vehicle.id} not found") }
        }
        return ResponseClientWithVehicles(
            id = this.id,
            clientName = this.clientName,
            clientSurname = this.clientSurname,
            gender = this.gender,
            jobTitle = this.jobTitle,
            company = this.company,
            phone = this.phone,
            email = this.email,
            address = this.address,
            vehicles = vehiclesWithIdAndNameList
        )
    }


}