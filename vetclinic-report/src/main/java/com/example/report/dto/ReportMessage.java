package com.example.report.dto;

import java.util.List;
import java.util.UUID;

public record ReportMessage(int schemaVersion, UUID reportId, List<EmployeeRow> employees) {
    public record EmployeeRow(Long id, String fullName, String qualification, String status) {
    }
}