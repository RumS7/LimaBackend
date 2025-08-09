package com.armylima.Lima.controllers;


import com.armylima.Lima.services.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;

    public  AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerAlert(Authentication auth) {
        alertService.triggerAlert(auth);
        return ResponseEntity.ok(Map.of("message", "Alert sent successfully"));
    }
}