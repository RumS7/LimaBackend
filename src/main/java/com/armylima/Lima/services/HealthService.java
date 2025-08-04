package com.armylima.Lima.services;


import com.armylima.Lima.dto.HealthReportDTO;
import com.armylima.Lima.dto.LeaveStatus;
import com.armylima.Lima.dto.OnLeaveHealthDTO;
import com.armylima.Lima.entities.HealthReport;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.HealthReportRepository;
import com.armylima.Lima.repositories.LeaveRequestRepository;
import com.armylima.Lima.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthService {

    private final LeaveRequestRepository leaveRepository;
    private final HealthReportRepository healthRepository;

    @Autowired
    public HealthService(LeaveRequestRepository leaveRepository, HealthReportRepository healthRepository) {
        this.leaveRepository = leaveRepository;
        this.healthRepository = healthRepository;
    }

    public List<OnLeaveHealthDTO> getHealthReportsForOnLeaveSoldiers() {
        LocalDate today = LocalDate.now();

        // Find all approved leaves that are currently active
        List<LeaveInfo> activeLeaves = leaveRepository.findByStatus(LeaveStatus.APPROVED).stream()
                .filter(leave -> !today.isBefore(leave.getFromDate()) && !today.isAfter(leave.getToDate()))
                .toList();

        // For each active leave, create a DTO with the soldier's health reports
        return activeLeaves.stream()
                .map(leave -> {
                    // --- CORRECTION ---
                    // Use the existing UserInfo relationship from the LeaveInfo object.
                    // This is much more efficient and avoids a new database query for every soldier.
                    UserInfo soldierInfo = leave.getUser();

                    List<HealthReport> reports = healthRepository.findByArmyId(soldierInfo.getArmyId());
                    return new OnLeaveHealthDTO(soldierInfo, reports);
                })
                .collect(Collectors.toList());
    }

    public HealthReport submitHealthReport(HealthReportDTO dto, Authentication auth) {
        String armyId = auth.getName();

        HealthReport report = HealthReport.builder()
                .armyId(armyId)
                .reportDate(LocalDate.now())
                .status(dto.getStatus())
                .symptoms(dto.getSymptoms())
                .build();

        return healthRepository.save(report);
    }
}