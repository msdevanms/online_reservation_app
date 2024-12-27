package com.tcs.bookingservice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingNumber = "DEFAULT_BOOKING_NUMBER";
    private String busNumber = "DEFAULT_BUS_NUMBER";
    private String source = "DEFAULT_SOURCE";
    private String destination = "DEFAULT_DESTINATION";
    private int numberOfSeats = 1; // Default to 1 seat
    private String status = "PENDING";

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers = new ArrayList<>();

    @PrePersist
    private void setDefaults() {
        if (this.bookingNumber == null) this.bookingNumber = "DEFAULT_BOOKING_NUMBER";
        if (this.busNumber == null) this.busNumber = "DEFAULT_BUS_NUMBER";
        if (this.source == null) this.source = "DEFAULT_SOURCE";
        if (this.destination == null) this.destination = "DEFAULT_DESTINATION";
        if (this.status == null) this.status = "PENDING";
    }
}
