package com.example.report.service;

import com.example.report.config.RabbitConfig;
import com.example.report.dto.ReportMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportMessagePublisher {

    private static final int MAX_MESSAGE_SIZE = 1024 * 1024;
    private final RabbitTemplate rabbitTemplate;

    public void publish(ReportMessage report) {
        CorrelationData correlation = new CorrelationData();

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, report, message -> {
                if (message.getBody().length > MAX_MESSAGE_SIZE) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "данные отчета превышают 1 МБ");
                }
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setMessageId(report.reportId().toString());
                return message;
            }, correlation);

            CorrelationData.Confirm confirm = correlation.getFuture().get(10, TimeUnit.SECONDS);

            if (!confirm.ack() || correlation.getReturned() != null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "брокер не подтвердил передачу отчета в очередь");
            }

            log.info("данные отчета переданы в очередь: reportId={}", report.reportId());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "ожидание подтверждения прервано", exception);
        } catch (AmqpException | ExecutionException | TimeoutException exception) {
            log.error("ошибка отправки отчета: reportId={}", report.reportId(), exception);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "не удалось подтвердить отправку отчета", exception);
        }
    }
}