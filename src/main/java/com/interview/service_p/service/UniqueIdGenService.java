package com.interview.service_p.service;
import org.springframework.stereotype.Service;

import java.util.UUID; // Import UUID for generating unique IDs

@Service
public class UniqueIdGenService {

    /**
     * Generates a universally unique identifier (UUID).
     * This ID can be used to uniquely identify Kafka messages or other entities.
     *
     * @return A unique string identifier.
     */
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
