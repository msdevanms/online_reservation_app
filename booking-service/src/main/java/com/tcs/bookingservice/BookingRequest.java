package com.tcs.bookingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class BookingRequest {
    private String bookingNumber;
    private String busNumber;
    private String source;
    private String destination;
    private int numberOfSeats;
    private List<String> passengers;
}
