package com.interview.service_p.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for health checks.
 * This class handles incoming HTTP requests related to application health.
 */
@RestController // Combines @Controller and @ResponseBody, suitable for RESTful APIs.
public class HealthController {

    /**
     * Handles GET requests to the "/health-check" endpoint.
     * This method provides a simple string response to indicate the service's status.
     *
     * @return A string message indicating that "service-a is Alive".
     */
    @GetMapping("/health-check") // Correct usage of @GetMapping for HTTP GET requests
    public String healthCheck() {
        return "service-p is alive";
    }
}