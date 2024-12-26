package com.tcs.inventoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/")
public class InventoryService   {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/inventory/{busNumber}")
    public int getAvailableSeats(@PathVariable String busNumber) {
        BusInventory inventory = inventoryRepository.findByBusNumber(busNumber);
        if (inventory == null) {
            // Handle the case where no inventory record exists for the bus
            return 0; // Or throw an exception
        }
        return inventory.getAvailableSeats();
    }
}

