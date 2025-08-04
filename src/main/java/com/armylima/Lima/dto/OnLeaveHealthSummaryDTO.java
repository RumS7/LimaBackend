package com.armylima.Lima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnLeaveHealthSummaryDTO {
    private int totalOnLeave;
    private int fitToday;
    private int notFitToday;
    private List<OnLeavePersonnelDTO> onLeavePersonnel;
}
