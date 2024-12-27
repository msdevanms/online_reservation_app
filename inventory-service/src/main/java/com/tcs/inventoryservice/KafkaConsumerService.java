package com.tcs.inventoryservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @Autowired
    private InventoryRepository inventoryRepository;


    @KafkaListener(topics = "booking-events", groupId = "inventory-service-group")
    public void consumeMessage(String message) throws JsonProcessingException {
        System.out.println("Received message: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        BookingRequest bookingRequest = objectMapper.readValue(message, BookingRequest.class);
        System.out.println("Received bookingRequest: " + bookingRequest.getNumberOfSeats());
        System.out.println("Received bookingRequest: " + bookingRequest.getBusNumber());
            // Find the BusInventory by busNumber
            BusInventory busInventory = inventoryRepository.findByBusNumber( bookingRequest.getBusNumber()).get();
            busInventory.setAvailableSeats(busInventory.getAvailableSeats() - bookingRequest.getNumberOfSeats());
            // Save the updated BusInventory
            inventoryRepository.save(busInventory);
    }
}
