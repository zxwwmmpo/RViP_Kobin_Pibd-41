package com.example.demo.service;

import com.example.demo.dto.EmployeeHireRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Status;
import com.example.demo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeResponse hireEmployee(EmployeeHireRequest request) {
        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setQualification(request.getQualification());
        employee.setStatus(Status.Working);

        Employee savedEmployee = repository.save(employee);
        return mapToResponse(savedEmployee);
    }

    public EmployeeResponse updateQualification(Long id, String newQualification) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "сотрудник не найден"));

        employee.setQualification(newQualification);
        return mapToResponse(repository.save(employee));
    }

    public EmployeeResponse fireEmployee(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "сотрудник не найден"));

        employee.setStatus(Status.Fired);
        return mapToResponse(repository.save(employee));
    }

    public Map<String, Long> getReport() {
        long working = repository.countByStatus(Status.Working);
        long fired = repository.countByStatus(Status.Fired);
        return Map.of("Рабочих", working, "Уволенных", fired);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "сотрудник не найден");
        }
        repository.deleteById(id);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFullName(employee.getFullName());
        response.setQualification(employee.getQualification());
        response.setStatus(employee.getStatus());
        return response;
    }
}