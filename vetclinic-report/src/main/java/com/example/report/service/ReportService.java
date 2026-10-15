package com.example.report.service;

import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final RestTemplate restTemplate;
    @Value("${employee-service.url}")
    private String employeeServiceUrl;

    public List<EmployeeReportResponse> getEmployees() {
        String url = employeeServiceUrl + "/api/employees";
        log.info("отправляем GET в app, {}", url);

        EmployeeReportResponse[] employees;
        try {
            employees = restTemplate.getForObject(
                    url,
                    EmployeeReportResponse[].class
            );
        } catch (RestClientException exception) {
            log.error("не получили список из app:", exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ошибка получения сотрудников из app", exception);


        }
        if (employees == null) {
            log.error("сервис app вернул ответ без тела");
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "сервис app вернул ответ без списка сотрудников");
        }

        return Arrays.stream(employees).sorted(Comparator.comparing(EmployeeReportResponse::getId)).toList();
    }

    public SummaryReportResponse getSummary() {
        log.info("!начато формирование отчета!");
        List<EmployeeReportResponse> employees = getEmployees();

        long working = employees.stream()
                .filter(employee -> employee.getStatus() == Status.Working).count();

        long fired = employees.stream()
                .filter(employee -> employee.getStatus() == Status.Fired).count();


        log.info("!отчет сформирован: working={}, fired={}", working, fired);

        return new SummaryReportResponse(working, fired);
    }

}
