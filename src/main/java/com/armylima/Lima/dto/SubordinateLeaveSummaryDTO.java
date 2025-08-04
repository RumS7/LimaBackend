package com.armylima.Lima.dto;

import com.armylima.Lima.entities.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubordinateLeaveSummaryDTO {

    private UserInfo subordinate;
    private long totalLeavesTaken;
    private LocalDate lastLeaveEndDate;
    private boolean isCurrentlyOnLeave;
}
