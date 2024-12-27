package com.tcs.compositorservice;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingRequest {
    private String busNumber;
    private int numberOfSeats;
    private String customerName;
    private String bookingNumber;

}
