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
import lombok.extern.slf4j.Slf4j;
import com.example.demo.dto.EmployeePageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeResponse hireEmployee(EmployeeHireRequest request) {
        log.info("начало принятия на работу");
        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setQualification(request.getQualification());
        employee.setStatus(Status.Working);
        log.info("SAVE");
        Employee savedEmployee = repository.save(employee);
        log.info("сотрудник сохранен: id={}", savedEmployee.getId());
        return mapToResponse(savedEmployee);
    }

    public EmployeeResponse updateQualification(Long id, String newQualification) {
        log.info("начало обновления квалификации");
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "сотрудник не найден"));

        employee.setQualification(newQualification);
        log.info("обновлена у: id={}", employee.getId());
        return mapToResponse(repository.save(employee));
    }

    public EmployeeResponse fireEmployee(Long id) {
        log.info("начало увольнения");
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "сотрудник не найден"));

        employee.setStatus(Status.Fired);
        log.info("уволен: id={}", employee.getId());
        return mapToResponse(repository.save(employee));
    }

    public Map<String, Long> getReport() {
        long working = repository.countByStatus(Status.Working);
        long fired = repository.countByStatus(Status.Fired);
        return Map.of("Рабочих", working, "Уволенных", fired);
    }

    public EmployeePageResponse getAllEmployees(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "номер страницы не может быть отрицательным");
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "размер страницы должен быть от 1 до 100");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<EmployeeResponse> employees = repository.findAll(pageable)
                .map(this::mapToResponse);

        return new EmployeePageResponse(
                employees.getContent(),
                employees.getNumber(),
                employees.getSize(),
                employees.getTotalElements(),
                employees.getTotalPages()
        );
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