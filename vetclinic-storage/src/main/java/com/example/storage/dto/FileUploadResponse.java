package com.example.storage.dto;

public record FileUploadResponse(
        String id,
        String fileName,
        long size
) {
}