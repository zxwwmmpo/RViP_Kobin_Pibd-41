package com.example.report.repository;

import com.example.report.entity.Employee;
import com.example.report.entity.Status;
import org.springframework.data.repository.Repository;
import java.util.List;


public interface EmployeeRepository extends Repository<Employee, Long> {

    List<Employee> findAllByOrderByIdAsc();

    long countByStatus(Status status);
}
