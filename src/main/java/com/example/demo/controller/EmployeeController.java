package com.example.demo.controller;

import com.example.demo.dto.EmployeeHireRequest;
import com.example.demo.dto.EmployeeQualificationRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Status;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "сотрудники", description = "управление сотрудниками")
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping("/hire")
    @Operation(summary = "принять")
    public EmployeeResponse hireEmployee(@Valid @RequestBody EmployeeHireRequest request) {
        return service.hireEmployee(request);
    }

    @PutMapping("/{id}/qualification")
    @Operation(summary = "обновить квалификацию")
    public EmployeeResponse updateQualification(@PathVariable Long id, @Valid @RequestBody EmployeeQualificationRequest request) {
        return service.updateQualification(id, request.getQualification());
    }

    @PutMapping("/{id}/fire")
    @Operation(summary = "уволить")
    public EmployeeResponse fireEmployee(@PathVariable Long id) {
        return service.fireEmployee(id);
    }

    @GetMapping("/report")
    @Operation(summary = "отчет")
    public Map<String, Long> getReport() {
        return service.getReport();
    }

    @GetMapping
    @Operation(summary = "список всех")
    public List<EmployeeResponse> getAllEmployees() {
        return service.getAllEmployees();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удалить работника")
    public void deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
    }
}
