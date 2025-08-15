package com.TestingApplication_Springboot.Testing.Application.repositories;

import com.TestingApplication_Springboot.Testing.Application.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    List<Employee> findByEmail(String email);
}
