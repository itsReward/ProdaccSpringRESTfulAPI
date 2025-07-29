package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.VehicleService
import org.prodacc.webapi.services.dataTransferObjects.NewVehicle
import org.prodacc.webapi.services.dataTransferObjects.ResponseVehicleWithClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/vehicles")
class VehicleController(
    private val vehicleService: VehicleService,
) {

    @GetMapping("/all")
    fun getVehicles(): Iterable<ResponseVehicleWithClient> = vehicleService.getVehicles()


    @GetMapping("/get/{id}")
    fun getVehicleById(@PathVariable id: UUID): ResponseVehicleWithClient = vehicleService.getVehicleById(id)


    @PostMapping("/new")
    fun addVehicle(@RequestBody vehicle: NewVehicle): ResponseVehicleWithClient = vehicleService.addVehicle(vehicle)


    @PutMapping("/update/{id}")
    fun updateVehicle(@PathVariable id: UUID, @RequestBody vehicle: NewVehicle): ResponseVehicleWithClient =
        vehicleService.updateVehicle(id, vehicle)


    @DeleteMapping("/delete/{id}")
    fun deleteVehicle(@PathVariable id: UUID): ResponseEntity<String> = vehicleService.deleteVehicle(id)

}