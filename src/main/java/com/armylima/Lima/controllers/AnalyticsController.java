package com.armylima.Lima.controllers;

import com.armylima.Lima.dto.IndividualLeaveStatsDTO;
import com.armylima.Lima.dto.OnLeaveHealthSummaryDTO;
import com.armylima.Lima.dto.SubordinateLeaveSummaryDTO;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.services.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PreAuthorize("hasAnyRole('KING','KNIGHT','QUEEN','ROOK','BISHOP')")
    @GetMapping("/subordinate-leave-summary")
    public ResponseEntity<List<SubordinateLeaveSummaryDTO>> getSubordinateSummary(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getSubordinateLeaveSummary(auth));
    }

    @PreAuthorize("hasAnyRole('KING','KNIGHT','QUEEN','ROOK','BISHOP')")
    @GetMapping("/on-leave-health-summary")
    public ResponseEntity<OnLeaveHealthSummaryDTO> getOnLeaveHealthSummary(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getOnLeaveHealthSummary(auth));
    }

    @PreAuthorize("hasAnyRole('KING','KNIGHT','QUEEN','ROOK','BISHOP')")
    @GetMapping("/leaves-in-last-days/{days}")
    public ResponseEntity<List<LeaveInfo>> getLeavesInLastDays(@PathVariable int days, Authentication auth) {
        return ResponseEntity.ok(analyticsService.getLeavesInLastDays(days, auth));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("individual-leave-stats")
    public ResponseEntity<List<IndividualLeaveStatsDTO>> getIndividualLeaveStats(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getIndividualLeaveStats(auth));
    }
}