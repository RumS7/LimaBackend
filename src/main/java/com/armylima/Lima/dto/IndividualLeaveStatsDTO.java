package com.armylima.Lima.dto;

import com.armylima.Lima.entities.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualLeaveStatsDTO {

    private UserInfo subordinate;
    private int totalLeaveDaysTakenThisYear;
    private int maxLeaveDaysPerYear;
    private int leaveDaysRemainingThisYear;
}
