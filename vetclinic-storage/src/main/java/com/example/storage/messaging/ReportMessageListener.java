package com.example.storage.messaging;

import com.example.storage.config.RabbitConfig;
import com.example.storage.dto.ReportMessage;
import com.example.storage.service.CsvReportService;
import com.example.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportMessageListener {

    private final CsvReportService csvReportService;
    private final FileStorageService fileStorageService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(ReportMessage report) throws IOException {
        try {
            validate(report);
            log.info("получены данные отчета: reportId={}", report.reportId());

            byte[] csv = csvReportService.createCsv(report.employees());
            fileStorageService.saveReport(report.reportId(), csv);

            log.info("отчет обработан: reportId={}", report.reportId());
        } catch (IllegalArgumentException exception) {
            log.error("некорректное сообщение отчета", exception);
            throw new AmqpRejectAndDontRequeueException("некорректные данные отчета", exception);
        }
    }

    private void validate(ReportMessage report) {
        if (report == null || report.reportId() == null || report.employees() == null) {
            throw new IllegalArgumentException("отсутствуют обязательные поля отчета");
        }
        if (report.schemaVersion() != 1) {
            throw new IllegalArgumentException("неподдерживаемая версия сообщения: " + report.schemaVersion());
        }
        if (report.employees().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("список сотрудников содержит null");
        }
    }
}