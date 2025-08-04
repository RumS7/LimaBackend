package com.armylima.Lima.controllers;


import com.armylima.Lima.dto.HealthReportDTO;
import com.armylima.Lima.dto.OnLeaveHealthDTO;
import com.armylima.Lima.entities.HealthReport;
import com.armylima.Lima.services.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @PreAuthorize("hasRole('OFFICER')")
    @GetMapping("/on-leave-soldiers")
    public ResponseEntity<List<OnLeaveHealthDTO>> getOnLeaveSoldiersHealth() {
        return ResponseEntity.ok(healthService.getHealthReportsForOnLeaveSoldiers());
    }

    @PreAuthorize("isAuthenticated()") // Any authenticated user can submit a report
    @PostMapping("/submit")
    public ResponseEntity<HealthReport> submitHealthReport(
            @RequestBody HealthReportDTO dto,
            Authentication auth
    ) {
        return ResponseEntity.ok(healthService.submitHealthReport(dto, auth));
    }
}
