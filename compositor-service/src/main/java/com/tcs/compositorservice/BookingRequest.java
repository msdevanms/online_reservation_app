package com.tcs.compositorservice;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingRequest {
    private String busNumber;
    private String source;
    private String destination;
    private int numberOfSeats;
    private String bookingNumber;

}
