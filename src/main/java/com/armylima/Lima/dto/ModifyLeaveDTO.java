package com.armylima.Lima.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ModifyLeaveDTO {
    private LocalDate newToDate;
    private String remarks;
}