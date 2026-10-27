package com.example.report.controller;

import com.example.report.dto.EmployeePageResponse;
import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.FileUploadResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.report.service.CsvReportService;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;
    private final CsvReportService csvReportService;

    @GetMapping("/employees")
    public EmployeePageResponse getEmployees(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        log.info("получен запрос отчета по сотрудникам: page={}, size={}", page, size);
        return service.getEmployeesPage(page, size);
    }

    @GetMapping("/summary")
    public SummaryReportResponse getSummary() {
        log.info("получен запрос сводного отчета");
        return service.getSummary();
    }

    @PostMapping("/employees/export")
    public ResponseEntity<FileUploadResponse> exportEmployees() {
        log.info("получен запрос на экспорт сотрудников в CSV");

        FileUploadResponse response = csvReportService.exportEmployees();

        return ResponseEntity.created(URI.create("/api/files/" + response.id())).body(response);
    }
}
