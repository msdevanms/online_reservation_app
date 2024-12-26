package com.tcs.adminservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class AdminService {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @GetMapping("/routes")
    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    @PostMapping("/routes")
    public ResponseEntity<String> addRoute(@RequestBody BusRoute busRoute) {
        try {
            // Check if a bus route with the same bus number already exists
            Optional<BusRoute> existingRoute = busRouteRepository.findByBusNumber(busRoute.getBusNumber());
            if (existingRoute.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Bus route with this number already exists.");
            }

            busRouteRepository.save(busRoute);
            return ResponseEntity.status(HttpStatus.CREATED).body("Bus route added successfully");
        } catch (Exception e) {
            // Log the exception and return an error message with appropriate status code
            System.err.println("Error adding bus route: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add bus route");
        }
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<String> updateRoute(@PathVariable Long id, @RequestBody BusRoute updatedRoute) {
        BusRoute existingRoute = busRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus route not found"));
        existingRoute.setSource(updatedRoute.getSource());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setPrice(updatedRoute.getPrice());
        busRouteRepository.save(existingRoute);
        return ResponseEntity.ok("Bus route updated successfully");
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        BusRoute busRoute = busRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus route not found"));
        busRouteRepository.delete(busRoute);
        return ResponseEntity.ok("Bus route deleted successfully");
    }
}

