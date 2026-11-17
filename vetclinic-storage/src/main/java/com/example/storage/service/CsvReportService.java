package com.example.storage.service;

import com.example.storage.dto.ReportMessage;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvReportService {

    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024;

    public byte[] createCsv(List<ReportMessage.EmployeeRow> employees) {
        ByteArrayOutputStream csv = new ByteArrayOutputStream();
        appendLine(csv, "id,fullName,qualification,status");

        for (var employee : employees) {
            String id = employee.id() == null ? "" : employee.id().toString();
            appendLine(csv, id + "," + cell(employee.fullName()) + "," + cell(employee.qualification()) + "," + cell(employee.status()));
        }

        return csv.toByteArray();
    }

    private void appendLine(ByteArrayOutputStream csv, String line) {
        byte[] bytes = (line + "\r\n").getBytes(StandardCharsets.UTF_8);

        if (bytes.length > MAX_FILE_SIZE - csv.size()) {
            throw new IllegalArgumentException("размер CSV превышает 20 МБ");
        }

        csv.writeBytes(bytes);
    }

    private String cell(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }
}