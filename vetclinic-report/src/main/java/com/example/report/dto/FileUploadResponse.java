package com.example.report.dto;

public record FileUploadResponse(
        String id,
        String fileName,
        long size
) {
}
