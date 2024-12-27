package com.tcs.inventoryservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // This will ignore any unknown properties in the incoming JSON
public class BookingRequest {

    private String busNumber;
    private int numberOfSeats;
    private String bookingNumber;
}
