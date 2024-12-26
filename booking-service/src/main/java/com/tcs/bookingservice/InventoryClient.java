package com.tcs.bookingservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryClient {

    @Autowired
    private RestTemplate restTemplate;

    public int getAvailableSeats(String busNumber) {
        String url = "http://localhost:8072/inventory-service/api/v1/inventory/" + busNumber;
        return restTemplate.getForObject(url, Integer.class);
    }
}