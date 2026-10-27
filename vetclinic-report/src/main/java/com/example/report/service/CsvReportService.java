package com.example.report.service;

import com.example.report.dto.EmployeeReportResponse;
import com.example.report.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvReportService {

    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024;

    private final ReportService reportService;
    private final RestTemplate restTemplate;

    @Value("${storage-service.url}")
    private String storageServiceUrl;

    public FileUploadResponse exportEmployees() {
        ByteArrayOutputStream csv = new ByteArrayOutputStream();
        appendLine(csv, "id,fullName,qualification,status");

        reportService.forEachEmployeePage(employees -> {
            for (EmployeeReportResponse employee : employees) {
                String status = employee.getStatus() == null ? "" : employee.getStatus().name();
                appendLine(csv, employee.getId() + "," + cell(employee.getFullName()) + "," + cell(employee.getQualification()) + "," + cell(status));
            }
        });

        log.info("CSV сформирован: size={}", csv.size());

        ByteArrayResource file = new ByteArrayResource(csv.toByteArray()) {
            @Override
            public String getFilename() {
                return "employees.csv";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            FileUploadResponse response = restTemplate.postForObject(storageServiceUrl + "/api/files", request, FileUploadResponse.class);

            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "сервис хранения вернул пустой ответ");
            }

            log.info("CSV сохранен: id={}", response.id());
            return response;
        } catch (RestClientException exception) {
            log.error("ошибка передачи CSV в хранилище", exception);

            if (exception instanceof HttpStatusCodeException httpException && httpException.getStatusCode().value() == 413) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "сервис хранения отклонил слишком большой файл", exception);
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "не удалось передать CSV в хранилище", exception);
        }
    }

    private void appendLine(ByteArrayOutputStream csv, String line) {
        byte[] bytes = (line + "\r\n").getBytes(StandardCharsets.UTF_8);

        if (bytes.length > MAX_FILE_SIZE - csv.size()) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "размер CSV превышает 20 МБ");
        }

        csv.writeBytes(bytes);
    }

    private String cell(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }
}