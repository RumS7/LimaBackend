package com.armylima.Lima.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class LegacyLeaveDTO {
    private String armyId; // The ID of the soldier we are adding a record for
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String location;
}