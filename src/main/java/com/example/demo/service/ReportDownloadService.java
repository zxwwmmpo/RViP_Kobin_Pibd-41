package com.example.demo.service;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportDownloadService {

    private final RestClient storageClient;

    public ReportDownloadService(@LoadBalanced RestClient.Builder storageClientBuilder) {
        this.storageClient = storageClientBuilder.baseUrl("http://vetclinic-storage").build();
    }

    @Async
    public CompletableFuture<ResponseEntity<byte[]>> getReport(UUID reportId) {
        ResponseEntity<byte[]> response = storageClient.get()
                .uri("/api/files/{id}", reportId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, result) -> {})
                .toEntity(byte[].class);

        return CompletableFuture.completedFuture(response);
    }
}