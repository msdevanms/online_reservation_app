package com.tcs.bookingservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        try {
            // Check seat availability with Inventory Service
            int availableSeats = inventoryClient.getAvailableSeats(request.getBusNumber());
            if (availableSeats < request.getNumberOfSeats()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BookingResponse("Not enough seats available.", "NA"));
            }

            // Create Booking entity
            Booking booking = new Booking();
            booking.setBookingNumber(UUID.randomUUID().toString());
            booking.setBusNumber(request.getBusNumber());
            booking.setSource(request.getSource());
            booking.setDestination(request.getDestination());
            booking.setNumberOfSeats(request.getNumberOfSeats());
            booking.setStatus("PENDING"); // Initial status

            // Save Booking to database
            Booking savedBooking = bookingRepository.save(booking);

            // Create and save Passengers (if provided in the request)
            if (request.getPassengers() != null && !request.getPassengers().isEmpty()) {
                List<Passenger> passengers = new ArrayList<>();
                for (String passengerName : request.getPassengers()) {
                    Passenger passenger = new Passenger();
                    passenger.setName(passengerName);
                    passenger.setBooking(savedBooking);
                    passengers.add(passenger);
                }
                savedBooking.setPassengers(passengers);
                bookingRepository.save(savedBooking); // Save with associated passengers
            }

            // Publish event to Kafka (for payment processing)
            // ... (Kafka integration would go here)

            return ResponseEntity.status(HttpStatus.CREATED).body(new BookingResponse(savedBooking.getBookingNumber(), savedBooking.getBusNumber()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponse("Failed to create booking.", "NA"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingNumber}")
    public ResponseEntity<Booking> getBookingByNumber(@PathVariable String bookingNumber) {
        Optional<Booking> optionalBooking = bookingRepository.findByBookingNumber(bookingNumber);
        if (optionalBooking.isPresent()) {
            return ResponseEntity.ok(optionalBooking.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    }

