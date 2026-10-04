package com.example.report.dto;

import com.example.report.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeReportResponse {
    private Long id;
    private String fullName;
    private String qualification;
    private Status status;
}
