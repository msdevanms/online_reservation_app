package com.tcs.inventoryservice;

import com.tcs.inventoryservice.BusInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<BusInventory, Long> {
    BusInventory findByBusNumber(String busNumber);
}

