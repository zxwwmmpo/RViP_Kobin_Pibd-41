package com.example.demo.dto;

import com.example.demo.entity.Status;
import lombok.Data;

@Data
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String qualification;
    private Status status;
}