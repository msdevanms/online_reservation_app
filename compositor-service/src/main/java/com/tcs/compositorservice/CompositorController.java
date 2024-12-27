package com.tcs.compositorservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CompositorController {

    @Autowired
    private CompositorService compositorService;

    /**
     * Endpoint to check seat availability, with caching.
     *
     * @param busNumber         The ID of the bus.
     * @param numberOfSeats The number of seats to check for.
     * @return A message indicating availability.
     */
    @GetMapping("/check")
    public String checkAvailability(@RequestParam String busNumber, @RequestParam int numberOfSeats) {
        return compositorService.checkAvailabilityAndCache(busNumber, numberOfSeats);
    }

    /**
     *
     * @param reserveRequest
     * @return
     */
    @PostMapping("/reserve")
    public String reserveSeats(@RequestBody ReserveRequest reserveRequest) {
        return compositorService.reserveSeats(reserveRequest.getBusNumber(), reserveRequest.getNumberOfSeats());
    }

    /**
     *
     * @param bookingRequest
     * @return
     */
    @PostMapping("/book")
    public String createBooking(@RequestBody BookingRequest bookingRequest) {
        return compositorService.createBooking(
                bookingRequest.getBusNumber(),
                bookingRequest.getNumberOfSeats(),
                bookingRequest.getCustomerName(),
                bookingRequest.getBookingNumber()
        );    }
}
