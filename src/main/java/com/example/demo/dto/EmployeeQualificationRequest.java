package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeQualificationRequest {
    @NotBlank(message = "квалификация обязательна")
    private String qualification;
}
