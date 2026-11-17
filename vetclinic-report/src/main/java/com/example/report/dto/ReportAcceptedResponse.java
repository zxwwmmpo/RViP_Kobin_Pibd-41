package com.example.report.dto;

import java.util.UUID;

public record ReportAcceptedResponse(UUID reportId, String downloadUrl) {
}