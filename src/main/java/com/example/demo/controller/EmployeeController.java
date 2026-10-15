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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "сотрудники", description = "управление сотрудниками")
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping("/hire")
    @Operation(summary = "принять")
    public EmployeeResponse hireEmployee(@Valid @RequestBody EmployeeHireRequest request) {
        log.info("получен запрос на принятие сотруднка");
        return service.hireEmployee(request);
    }

    @PutMapping("/{id}/qualification")
    @Operation(summary = "обновить квалификацию")
    public EmployeeResponse updateQualification(@PathVariable Long id, @Valid @RequestBody EmployeeQualificationRequest request) {
        log.info("получен запрос на квалификацию");
        return service.updateQualification(id, request.getQualification());
    }

    @PutMapping("/{id}/fire")
    @Operation(summary = "уволить")
    public EmployeeResponse fireEmployee(@PathVariable Long id) {
        log.info("получен запрос на уволльнение сотруднка");
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
        log.info("получен GET ");
        List<EmployeeResponse> employees = service.getAllEmployees();
        log.info("отправляем список. кол-воЖ {}", employees.size());
        return employees;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удалить работника")
    public void deleteEmployee(@PathVariable Long id) {
        log.info("получен запрос на удаление сотруднка из бд");
        service.deleteEmployee(id);
    }
}
