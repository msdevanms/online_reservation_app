package com.tcs.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Qualifier("jacksonObjectMapper")


    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> reserveSeats(@RequestBody BookingRequest request) {
        try {
            // Check seat availability with Inventory Service
            int availableSeats = inventoryClient.getAvailableSeats(request.getBusNumber());
            if (availableSeats < request.getNumberOfSeats()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BookingResponse("Not enough seats available to reserve.", "NA"));
            }

            // Create Reservation entity (not confirmed yet)
            Booking reservation = new Booking();
            reservation.setBookingNumber(UUID.randomUUID().toString());
            reservation.setBusNumber(request.getBusNumber());
            reservation.setSource(request.getSource());
            reservation.setDestination(request.getDestination());
            reservation.setNumberOfSeats(request.getNumberOfSeats());
            reservation.setStatus("RESERVED"); // Status indicating reservation

            // Save Reservation to database
            Booking savedReservation = bookingRepository.save(reservation);

            // Create and save Passengers (if provided in the request)
            if (request.getPassengers() != null && !request.getPassengers().isEmpty()) {
                List<Passenger> passengers = new ArrayList<>();
                for (String passengerName : request.getPassengers()) {
                    Passenger passenger = new Passenger();
                    passenger.setName(passengerName);
                    passenger.setBooking(savedReservation);
                    passengers.add(passenger);
                }
                savedReservation.setPassengers(passengers);
                bookingRepository.save(savedReservation); // Save with associated passengers
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new BookingResponse(savedReservation.getBookingNumber(), "Seats reserved successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponse("Failed to reserve seats.", "NA"));
        }
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookSeats(@RequestBody BookingRequest request) {
        try {
            // Check if the reservation exists and is in the RESERVED state
            Optional<Booking> optionalReservation = bookingRepository.findByBookingNumber(request.getBookingNumber());
            if (optionalReservation.isPresent()) {
                Booking reservation = optionalReservation.get();
                if ("RESERVED".equals(reservation.getStatus())) {

                    // Confirm booking and process payment (simulated here)
                    reservation.setStatus("CONFIRMED");

                    // Save final Booking (confirmed status)
                    Booking confirmedBooking = bookingRepository.save(reservation);

                    String jsonMessage = String.format(
                            "{\n" +
                                    "    \"busNumber\": \"%s\",\n" +
                                    "    \"numberOfSeats\": %d,\n" +
                                    "    \"bookingNumber\": \"%s\"\n" +
                                    "}",
                            confirmedBooking.getBusNumber(), confirmedBooking.getNumberOfSeats(), confirmedBooking.getBookingNumber()
                    );

                    // Publish event to Kafka (for inventory update)
                    String bookingDetails =  jsonMessage;

                    kafkaProducerService.sendMessage(bookingDetails);

                    return ResponseEntity.status(HttpStatus.CREATED).body(new BookingResponse(confirmedBooking.getBookingNumber(), "Booking confirmed."));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new BookingResponse("Reservation already confirmed or invalid.", "NA"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BookingResponse("Reservation not found.", "NA"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponse("Failed to book the reservation.", "NA"));
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
