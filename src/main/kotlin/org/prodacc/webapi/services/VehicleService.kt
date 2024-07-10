package org.prodacc.webapi.services

import jakarta.persistence.EntityNotFoundException
import org.prodacc.webapi.models.Vehicle
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.dataTransferObjects.NewVehicle
import org.prodacc.webapi.services.dataTransferObjects.ResponseVehicleWithClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
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
        if (newVehicle.clientReference == null) {
            throw EntityNotFoundException("Vehicle owner not input, INPUT VEHICLE OWNER")
        } else {
            return vehicleRepository.save(newVehicle.toVehicle()).toVehicleWithClientIdAndName()
        }


    }


    @Transactional
    fun updateVehicle( id: UUID,  vehicle: NewVehicle): ResponseVehicleWithClient {
        val existingVehicle = vehicleRepository
            .findById(id)
            .orElseThrow { EntityNotFoundException("Vehicle with id $id not found") }
        val client = vehicle.clientReference?.let { clientRepository.findById(it).get() }
        val updatedVehicle = existingVehicle.copy(
            model = vehicle.model?:existingVehicle.model,
            regNumber = vehicle.regNumber?:existingVehicle.regNumber,
            make = vehicle.make?:existingVehicle.make,
            color = vehicle.make?:existingVehicle.color,
            chassisNumber = vehicle.chassisNumber?:existingVehicle.chassisNumber,
            clientReference = client?:existingVehicle.clientReference
        )
        return vehicleRepository.save(updatedVehicle).toVehicleWithClientIdAndName()

    }


    @Transactional
    fun deleteVehicle( id: UUID): ResponseEntity<String> {
        return if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id)
            ResponseEntity("Vehicle deleted successfully", HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Vehicle with id $id not found")
        }
    }


    private fun NewVehicle.toVehicle(): Vehicle {
        val client = if ( this.clientReference != null ) {
            clientRepository
                .findById(this.clientReference)
                .orElseThrow { EntityNotFoundException("Client with id ${this.clientReference} not found") }
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