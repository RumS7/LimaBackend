package com.armylima.Lima.dto;


import lombok.Data;

@Data
public class HealthReportDTO {
    private HealthStatus status;
    private String symptoms;
}