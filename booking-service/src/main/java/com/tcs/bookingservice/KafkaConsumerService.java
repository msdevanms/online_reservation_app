package com.tcs.bookingservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "booking-events", groupId = "group_id")
    public void consumeMessage(String message) {
        System.out.println("Received message: " + message);
        // Process the message as needed
    }
}
