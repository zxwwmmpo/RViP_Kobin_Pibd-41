package com.example.report.dto;

import java.util.List;

public record EmployeePageResponse(
        List<EmployeeReportResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}