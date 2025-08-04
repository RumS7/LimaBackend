package com.armylima.Lima.dto;


import com.armylima.Lima.entities.HealthReport;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.entities.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnLeavePersonnelDTO {
    private UserInfo soldier;
    private LeaveInfo currentLeave;
    private HealthReport latestHealthReport; // Can be null
}