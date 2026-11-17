package com.example.demo.controller;

import com.example.demo.service.ReportDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/employees/reports")
@RequiredArgsConstructor
public class ReportDownloadController {

    private final ReportDownloadService reportDownloadService;

    @GetMapping("/{reportId}")
    public CompletableFuture<ResponseEntity<byte[]>> download(@PathVariable("reportId") UUID reportId) {
        return reportDownloadService.getReport(reportId);
    }
}