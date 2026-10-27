package com.example.report.service;

import com.example.report.dto.EmployeePageResponse;
import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.SummaryReportResponse;
import com.example.report.entity.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int PAGE_SIZE = 100;
    private final RestTemplate restTemplate;

    @Value("${employee-service.url}")
    private String employeeServiceUrl;

    public EmployeePageResponse getEmployeesPage(int page, int size) {
        String url = employeeServiceUrl + "/api/employees?page={page}&size={size}";
        log.info("запрашиваем страницу сотрудников: page={}, size={}", page, size);
        EmployeePageResponse response;

        try {
            response = restTemplate.getForObject(
                    url,
                    EmployeePageResponse.class,
                    page,
                    size
            );
        } catch (RestClientException exception) {
            log.error("ошибка получения страницы сотрудников: page={}", page, exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ошибка получения сотрудников из app", exception);
        }

        if (response == null || response.content() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "сервис app вернул ответ без списка сотрудников");
        }

        return response;
    }

    public List<EmployeeReportResponse> getEmployees() {
        List<EmployeeReportResponse> employees = new ArrayList<>();
        int pageNumber = 0;

        while (true) {
            EmployeePageResponse page = getEmployeesPage(pageNumber, PAGE_SIZE);
            employees.addAll(page.content());

            if (page.content().isEmpty() || pageNumber + 1 >= page.totalPages()) {
                break;
            }
            pageNumber++;
        }

        log.info("получен полный список сотрудников: количество={}", employees.size());

        return employees;
    }

    public SummaryReportResponse getSummary() {
        log.info("начато формирование сводного отчета");

        long working = 0;
        long fired = 0;
        int pageNumber = 0;

        while (true) {
            EmployeePageResponse page = getEmployeesPage(pageNumber, PAGE_SIZE);

            for (EmployeeReportResponse employee : page.content()) {
                if (employee.getStatus() == Status.Working) {
                    working++;
                } else if (employee.getStatus() == Status.Fired) {
                    fired++;
                }
            }

            if (page.content().isEmpty() || pageNumber + 1 >= page.totalPages()) {
                break;
            }

            pageNumber++;
        }

        log.info("сводный отчет сформирован: working={}, fired={}", working, fired);

        return new SummaryReportResponse(working, fired);
    }
}