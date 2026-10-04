package com.example.report.service;

import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.entity.Status;
import com.example.report.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeeRepository repository;

    public List<EmployeeReportResponse> getEmployees() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(employee -> new EmployeeReportResponse(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getQualification(),
                        employee.getStatus()
                ))
                .toList();
    }

    public SummaryReportResponse getSummary() {
        long working = repository.countByStatus(Status.Working);
        long fired = repository.countByStatus(Status.Fired);
        return new SummaryReportResponse(working, fired);
    }

}
