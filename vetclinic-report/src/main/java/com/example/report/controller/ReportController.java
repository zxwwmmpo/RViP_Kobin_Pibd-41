package com.example.report.controller;

import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/employees")
    public List<EmployeeReportResponse> getEmployees() {
        log.info("принят GET на список");
        return service.getEmployees();
    }

    @GetMapping("/summary")
    public SummaryReportResponse getSummary() {
        log.info("принят GET на подсчет");
        return service.getSummary();
    }
}
