package com.example.report.service;

import com.example.report.dto.ReportMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportService reportService;
    private final ReportMessagePublisher publisher;

    public UUID exportEmployees() {
        UUID reportId = UUID.randomUUID();
        List<ReportMessage.EmployeeRow> rows = new ArrayList<>();

        reportService.forEachEmployeePage(employees -> {
            for (var employee : employees) {
                String status = employee.getStatus() == null ? "" : employee.getStatus().name();
                rows.add(new ReportMessage.EmployeeRow(employee.getId(), employee.getFullName(), employee.getQualification(), status));
            }
        });

        publisher.publish(new ReportMessage(1, reportId, rows));
        return reportId;
    }
}