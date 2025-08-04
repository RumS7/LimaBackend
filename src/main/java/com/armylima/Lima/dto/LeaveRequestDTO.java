package com.armylima.Lima.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
}
