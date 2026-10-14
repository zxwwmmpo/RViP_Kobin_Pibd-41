package com.example.report.service;

import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.entity.Status;
import com.example.report.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeeRepository repository;

    public List<EmployeeReportResponse> getEmployees() {
        log.info("запрос списка сотрудников из бд");
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
        log.info("!начато формирование отчета!");
        long working = repository.countByStatus(Status.Working);
        long fired = repository.countByStatus(Status.Fired);

        log.info("!отчет сформирован: working={}, fired={}", working, fired);

        return new SummaryReportResponse(working, fired);
    }

}
