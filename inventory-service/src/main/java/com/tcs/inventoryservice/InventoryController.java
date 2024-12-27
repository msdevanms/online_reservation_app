package com.tcs.inventoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<BusInventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAvailability(@RequestParam String busNumber, @RequestParam int numberOfSeats) {
        boolean isAvailable = inventoryService.checkAvailability(busNumber, numberOfSeats);
        return ResponseEntity.ok(isAvailable);
    }

    // Reserve seats
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeats(@RequestParam String busNumber, @RequestParam int numberOfSeats) {
        boolean success = inventoryService.reserveSeats(busNumber, numberOfSeats);

        if (success) {
            return ResponseEntity.ok("Seats reserved successfully");
        } else {
            return ResponseEntity.status(400).body("Failed to reserve seats");
        }
    }
    // Get a bus inventory by ID
    @GetMapping("/{busNumber}")
    public ResponseEntity<Integer> getAvailableSeatsByBusNumber(@PathVariable String busNumber) {
        // Retrieve the BusInventory based on busNumber
        return inventoryRepository.findByBusNumber(busNumber)
                .map(busInventory -> ResponseEntity.ok(busInventory.getAvailableSeats()))  // Return only availableSeats
                .orElseGet(() -> ResponseEntity.ok(0));  // Return 0 if not found
    }

    // Create a new bus inventory
    @PostMapping
    public ResponseEntity<BusInventory> createInventory(@RequestBody BusInventory busInventory) {
        BusInventory savedInventory = inventoryRepository.save(busInventory);
        return new ResponseEntity<>(savedInventory, HttpStatus.CREATED);
    }

    // Update an existing bus inventory
    @PutMapping("/{busNumber}")
    public ResponseEntity<BusInventory> updateInventory(@PathVariable String busNumber, @RequestBody BusInventory updatedInventory) {
        return inventoryRepository.findByBusNumber(busNumber).map(existingInventory -> {
            existingInventory.setBusNumber(updatedInventory.getBusNumber());
            existingInventory.setTotalSeats(updatedInventory.getTotalSeats());
            existingInventory.setAvailableSeats(updatedInventory.getAvailableSeats());
            inventoryRepository.save(existingInventory);
            return new ResponseEntity<>(existingInventory, HttpStatus.OK);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a bus inventory
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

