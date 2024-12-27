package com.tcs.inventoryservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<BusInventory, Long> {
    Optional<BusInventory> findByBusNumber(String busNumber);
}

