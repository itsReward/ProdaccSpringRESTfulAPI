package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Vehicle
import org.prodacc.webapi.models.dataTransferObjects.UpdateVehicle
import org.prodacc.webapi.models.dataTransferObjects.VehicleWithClientIdAndName
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/v1/vehicles")
class VehicleController(
    private val repository: VehicleRepository,
    private val clientRepository: ClientRepository
) {

    @GetMapping("/all")
    fun getVehicles(): Iterable<VehicleWithClientIdAndName> {
        return repository.findAll().map { vehicle: Vehicle ->
            VehicleWithClientIdAndName(
                id = vehicle.id,
                model = vehicle.model,
                regNumber = vehicle.regNumber,
                make = vehicle.make,
                color = vehicle.color,
                chassisNumber = vehicle.chassisNumber,
                clientId = vehicle.clientReference?.id,
                clientName = vehicle.clientReference?.clientName,
                clientSurname = vehicle.clientReference?.clientSurname
            )
        }
    }


    @GetMapping("/get/{id}")
    fun getVehicleById(@PathVariable id: UUID): Optional<Vehicle> = repository.findById(id)


    @PostMapping("/new")
    fun addVehicle(@RequestBody vehicle: Vehicle): ResponseEntity<Any> {
        if (vehicle.clientReference == null || vehicle.clientReference!!.id == null) {
            return ResponseEntity("Client reference is missing or invalid", HttpStatus.BAD_REQUEST)
        }

        val clientId = vehicle.clientReference!!.id!!
        val client = clientRepository.findById(clientId)
        if (client.isEmpty) {
            return ResponseEntity("Client not found", HttpStatus.NOT_FOUND)
        }

        vehicle.clientReference = client.get()
        val savedVehicle = repository.save(vehicle)

        val response = VehicleWithClientIdAndName(
            id = savedVehicle.id,
            model = savedVehicle.model,
            regNumber = savedVehicle.regNumber,
            make = savedVehicle.make,
            color = savedVehicle.color,
            chassisNumber = savedVehicle.chassisNumber,
            clientId = savedVehicle.clientReference?.id,
            clientName = savedVehicle.clientReference?.clientName,
            clientSurname = savedVehicle.clientReference?.clientSurname
        )

        return ResponseEntity.ok(response)
    }


    @PutMapping("/update/{id}")
    fun updateVehicle(@PathVariable id: UUID, @RequestBody vehicle: UpdateVehicle): ResponseEntity<VehicleWithClientIdAndName> {
        return repository.findById(id).map { existingVehicle ->
            val updatedVehicle = existingVehicle.copy(
                model = vehicle.model?:existingVehicle.model,
                regNumber = vehicle.regNumber?:existingVehicle.regNumber,
                make = vehicle.make?:existingVehicle.make,
                color = vehicle.make?:existingVehicle.color,
                chassisNumber = vehicle.chassisNumber?:existingVehicle.chassisNumber,
                clientReference = vehicle.clientReference?:existingVehicle.clientReference
            )
            val client = clientRepository.findById(updatedVehicle.clientReference!!.id!!)
            repository.save(updatedVehicle)

            ResponseEntity.ok().body(VehicleWithClientIdAndName(
                id = updatedVehicle.id,
                model = updatedVehicle.model,
                regNumber = updatedVehicle.regNumber,
                make = updatedVehicle.make,
                color = updatedVehicle.color,
                chassisNumber = updatedVehicle.chassisNumber,
                clientId = client.get().id,
                clientName = client.get().clientName,
                clientSurname = client.get().clientSurname
            ))
        }.orElse(ResponseEntity.notFound().build())
    }


    @DeleteMapping("/delete/{id}")
    fun deleteVehicle(@PathVariable id: UUID): ResponseEntity<String> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity("Vehicle deleted successfully", HttpStatus.OK)
        } else {
            ResponseEntity("Vehicle not found", HttpStatus.NOT_FOUND)
        }
    }

}