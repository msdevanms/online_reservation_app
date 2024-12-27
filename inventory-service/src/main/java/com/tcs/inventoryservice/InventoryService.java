package com.tcs.inventoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Check seat availability
    public boolean checkAvailability(String busNumber, int numberOfSeats) {
        Optional<BusInventory> busInventoryOptional = inventoryRepository.findByBusNumber(busNumber);

        return busInventoryOptional
                .map(busInventory -> busInventory.getAvailableSeats() >= numberOfSeats)
                .orElse(false);
    }

    // Reserve seats
    public boolean reserveSeats(String busNumber, int numberOfSeats) {
        Optional<BusInventory> busInventoryOptional = inventoryRepository.findByBusNumber(busNumber);

        if (busInventoryOptional.isPresent()) {
            BusInventory busInventory = busInventoryOptional.get();

            if (busInventory.getAvailableSeats() >= numberOfSeats) {
                // Reserve seats
                busInventory.setAvailableSeats(busInventory.getAvailableSeats() - numberOfSeats);
                inventoryRepository.save(busInventory);
                return true;
            }
        }
        return false;
    }
}
