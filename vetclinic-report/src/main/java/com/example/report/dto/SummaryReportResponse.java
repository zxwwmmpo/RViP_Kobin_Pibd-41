package com.example.report.dto;

import com.example.report.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryReportResponse {
    private long working;
    private long fired;
}
