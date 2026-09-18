package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "данные для приема на работу")
public class EmployeeHireRequest {
    @Schema(description = "ФИО", example = "Иванов Иван Иванович")
    @NotBlank(message = "ФИО обязательно")
    private String fullName;

    @Schema(description = "квалификация", example = "ветеринар")
    @NotBlank(message = "квалификация обязательно")
    private String qualification;
}