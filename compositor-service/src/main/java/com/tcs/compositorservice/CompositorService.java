package com.tcs.compositorservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompositorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    // Gateway URLs for Inventory and Booking Services
    private final String INVENTORY_SERVICE_URL = "http://localhost:8072/inventory-service/api/v1/inventory";
    private final String BOOKING_SERVICE_URL = "http://localhost:8072/booking-service/api/v1/bookings";


    public String checkAvailabilityAndCache(String busNumber, int numberOfSeats) {
        String cacheKey = generateCacheKey(busNumber, numberOfSeats);

        // Check if availability is cached
        Boolean cachedAvailability = (Boolean) redisTemplate.opsForValue().get(cacheKey);

        if (cachedAvailability != null) {
            // If cached, return the cached result
            return cachedAvailability ? "Seats are available (cached)" : "Seats are not available (cached)";
        }

        // If not cached, call Inventory Service to check availability
        String url = INVENTORY_SERVICE_URL + "/check?busNumber=" + busNumber + "&numberOfSeats=" + numberOfSeats;
        Boolean isAvailable = restTemplate.getForObject(url, Boolean.class);

        // Cache the result for future requests
        redisTemplate.opsForValue().set(cacheKey, isAvailable);

        return isAvailable ? "Seats are available" : "Seats are not available";
    }


    /**
     * Method to reserve seats.
     * Calls Inventory Service to reserve seats.
     */
    public String reserveSeats(String busNumber, int numberOfSeats) {
        String url = BOOKING_SERVICE_URL + "/reserve";
        ReserveRequest reserveRequest = new ReserveRequest(busNumber, numberOfSeats);

        try {
            String response = restTemplate.postForObject(url, reserveRequest, String.class);

            // Invalidate the cache after reserving seats (since availability may have changed)
            String cacheKey = generateCacheKey(busNumber, numberOfSeats);
            redisTemplate.delete(cacheKey);

            return response;  // Successfully reserved seats
        } catch (Exception e) {
            return "Error during seat reservation: " + e.getMessage();
        }
    }

    /**
     * Method to create a booking.
     * Calls Booking Service to create the booking after seat reservation.
     */
    public String createBooking(String busNumber,String source,String destination,int numberOfSeat,  String bookingNumber) {
        // First, check and reserve seats
        String availabilityResponse = checkAvailabilityAndCache(busNumber, numberOfSeat);
        if (availabilityResponse.contains("not available")) {
            return "Seats not available. Booking cannot be created.";
        }

        String reserveResponse = reserveSeats(busNumber, numberOfSeat);
        if (reserveResponse.contains("Error")) {
            return reserveResponse;  // If reserving failed
        }

        // After reserving seats, create the booking
        String url = BOOKING_SERVICE_URL + "/book";
        BookingRequest bookingRequest = new BookingRequest(busNumber,source,destination,numberOfSeat,bookingNumber);
        try {
            return restTemplate.postForObject(url, bookingRequest, String.class);
        } catch (Exception e) {
            return "Error during booking creation: " + e.getMessage();
        }
    }

    /**
     * Helper method to generate a cache key based on busNumber and numberOfSeats.
     */
    private String generateCacheKey(String busNumber, int numberOfSeats) {
        return "seat_availability:" + busNumber + ":" + numberOfSeats;
    }
}
