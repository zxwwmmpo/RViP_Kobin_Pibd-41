package com.example.demo.repository;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long countByStatus(Status status);
}
