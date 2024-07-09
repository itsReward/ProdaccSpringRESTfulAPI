package org.prodacc.webapi.controllers

import org.prodacc.webapi.models.Vehicle
import org.prodacc.webapi.models.dataTransferObjects.NewVehicle
import org.prodacc.webapi.models.dataTransferObjects.VehicleWithClientIdAndName
import org.prodacc.webapi.repositories.ClientRepository
import org.prodacc.webapi.repositories.VehicleRepository
import org.prodacc.webapi.services.VehicleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/v1/vehicles")
class VehicleController(
    private val vehicleService: VehicleService,
) {

    @GetMapping("/all")
    fun getVehicles(): Iterable<VehicleWithClientIdAndName> = vehicleService.getVehicles()


    @GetMapping("/get/{id}")
    fun getVehicleById(@PathVariable id: UUID): VehicleWithClientIdAndName = vehicleService.getVehicleById(id)


    @PostMapping("/new")
    fun addVehicle(@RequestBody vehicle: NewVehicle): VehicleWithClientIdAndName = vehicleService.addVehicle(vehicle)


    @PutMapping("/update/{id}")
    fun updateVehicle(@PathVariable id: UUID, @RequestBody vehicle: NewVehicle): VehicleWithClientIdAndName =
        vehicleService.updateVehicle(id, vehicle)


    @DeleteMapping("/delete/{id}")
    fun deleteVehicle(@PathVariable id: UUID): ResponseEntity<String> = vehicleService.deleteVehicle(id)

}