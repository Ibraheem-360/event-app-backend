package com.event.management.event_management.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BACKEND_URL = "https://event-app-backend-1.onrender.com/actuator/health";


    @Scheduled(fixedRate = 100000)
    public void pingBackend() {
        try {
            restTemplate.getForObject(BACKEND_URL, String.class);
            System.out.println("Pinged backend successfully.");
        } catch (Exception e) {
            System.err.println("Failed to ping backend: " + e.getMessage());
        }
    }
}
