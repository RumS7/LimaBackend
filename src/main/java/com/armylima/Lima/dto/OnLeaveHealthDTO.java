package com.armylima.Lima.dto;

import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.entities.HealthReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnLeaveHealthDTO {
    private UserInfo soldier;
    private List<HealthReport> healthReports;
}
