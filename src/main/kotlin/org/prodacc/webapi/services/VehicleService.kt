package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Vehicle
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.NewVehicle
import org.prodacc.webapi.services.dataTransferObjects.ResponseVehicleWithClient
import org.prodacc.webapi.services.synchronisation.WebSocketHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository,
    private val webSocketHandler: WebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(VehicleService::class.java)

    fun getVehicles(): Iterable<ResponseVehicleWithClient> {
        logger.info("Fetching all vehicles")
        return vehicleRepository.findAll().map { it.toVehicleWithClientIdAndName() }
    }




    fun getVehicleById( id: UUID): ResponseVehicleWithClient {
        return vehicleRepository.findById(id)
            .map { it.toVehicleWithClientIdAndName() }
            .orElseThrow { EntityNotFoundException("Vehicle with id $id not found") }
    }


    @Transactional
    fun addVehicle(newVehicle: NewVehicle): ResponseVehicleWithClient {
        logger.info("Adding new vehicle")
        if (newVehicle.clientId == null) {
            throw EntityNotFoundException("Vehicle owner not input, INPUT VEHICLE OWNER")
        } else {
            val responseVehicle = vehicleRepository.save(newVehicle.toVehicle()).toVehicleWithClientIdAndName()
            webSocketHandler.broadcastUpdate("NEW_VEHICLE", responseVehicle.id!!)
            return responseVehicle
        }


    }


    @Transactional
    fun updateVehicle( id: UUID,  vehicle: NewVehicle): ResponseVehicleWithClient {
        val existingVehicle = vehicleRepository
            .findById(id)
            .orElseThrow { EntityNotFoundException("Vehicle with id $id not found") }
        val client = vehicle.clientId?.let { clientRepository.findById(it).get() }
        val updatedVehicle = existingVehicle.copy(
            model = vehicle.model?:existingVehicle.model,
            regNumber = vehicle.regNumber?:existingVehicle.regNumber,
            make = vehicle.make?:existingVehicle.make,
            color = vehicle.color?:existingVehicle.color,
            chassisNumber = vehicle.chassisNumber?:existingVehicle.chassisNumber,
            clientReference = client?:existingVehicle.clientReference
        )
        val responseVehicle = vehicleRepository.save(updatedVehicle).toVehicleWithClientIdAndName()
        webSocketHandler.broadcastUpdate("UPDATE_VEHICLE", responseVehicle.id!!)
        return responseVehicle

    }


    @Transactional
    fun deleteVehicle( id: UUID): ResponseEntity<String> {
        return if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id)
            webSocketHandler.broadcastUpdate("DELETE_VEHICLE", id)
            ResponseEntity("Vehicle deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Vehicle with id $id not found")
        }
    }


    private fun NewVehicle.toVehicle(): Vehicle {
        val client = if ( this.clientId != null ) {
            clientRepository
                .findById(this.clientId)
                .orElseThrow { EntityNotFoundException("Client with id ${this.clientId} not found") }
        } else throw NullPointerException("Client is can not be null, INPUT VEHICLE OWNER")

        return Vehicle(
            model = this.model,
            regNumber = this.regNumber,
            make = this.make,
            color = this.color,
            chassisNumber = this.chassisNumber,
            clientReference = client
        )
    }

    private fun Vehicle.toVehicleWithClientIdAndName(): ResponseVehicleWithClient {
        return ResponseVehicleWithClient(
            id = this.id,
            model = this.model,
            regNumber = this.regNumber,
            make = this.make,
            color = this.color,
            chassisNumber = this.chassisNumber,
            clientId = this.clientReference?.id,
            clientName = this.clientReference?.clientName,
            clientSurname = this.clientReference?.clientSurname
        )
    }

}