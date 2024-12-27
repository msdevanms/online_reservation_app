package com.tcs.bookingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class BookingResponse {
    private String bookingNumber;
    private String message;

}