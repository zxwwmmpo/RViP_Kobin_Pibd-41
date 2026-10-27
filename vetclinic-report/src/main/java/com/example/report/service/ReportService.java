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

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;

    @Value("${employee-service.url}")
    private String employeeServiceUrl;

    public EmployeePageResponse getEmployeesPage(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "номер страницы не может быть отрицательным");
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "размер страницы должен быть от 1 до 100");
        }

        String url = employeeServiceUrl + "/api/employees?page={page}&size={size}";

        log.info("запрашиваем страницу сотрудников: page={}, size={}", page, size);

        EmployeePageResponse response;

        try {
            response = restTemplate.getForObject(url, EmployeePageResponse.class, page, size);
        } catch (RestClientException exception) {
            log.error("ошибка получения страницы сотрудников: page={}", page, exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ошибка получения сотрудников из app", exception);
        }

        if (response == null || response.content() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "сервис app вернул ответ без списка сотрудников");
        }

        return response;
    }

    public void forEachEmployeePage(Consumer<List<EmployeeReportResponse>> action) {
        int pageNumber = 0;

        while (true) {
            EmployeePageResponse page = getEmployeesPage(pageNumber, PAGE_SIZE);
            if (page.content().isEmpty()) {
                break;
            }
            action.accept(page.content());
            if (pageNumber + 1 >= page.totalPages()) {
                break;
            }

            pageNumber++;
        }
    }

    public SummaryReportResponse getSummary() {
        log.info("начато формирование сводного отчета");
        long[] counts = new long[2];

        forEachEmployeePage(employees -> {
            for (EmployeeReportResponse employee : employees) {
                if (employee.getStatus() == Status.Working) {
                    counts[0]++;
                } else if (employee.getStatus() == Status.Fired) {
                    counts[1]++;
                }
            }
        });

        log.info("сводный отчет сформирован: working={}, fired={}", counts[0], counts[1]);

        return new SummaryReportResponse(counts[0], counts[1]);
    }
}