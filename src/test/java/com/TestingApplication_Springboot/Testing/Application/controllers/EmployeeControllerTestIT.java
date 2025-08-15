package com.TestingApplication_Springboot.Testing.Application.controllers;

import com.TestingApplication_Springboot.Testing.Application.dto.EmployeeDto;
import com.TestingApplication_Springboot.Testing.Application.entities.Employee;
import com.TestingApplication_Springboot.Testing.Application.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeControllerTestIT extends  AbstractIntegrationTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }


    @Test
    void testGetEmployeeById_success(){
        Employee savedEmployee=employeeRepository.save(testEmployee);

        webTestClient
                .get()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(savedEmployee.getName())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());

    }

   @Test
    void testGetEmployeeById_Failure(){
        webTestClient
                .get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
   }

   @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException(){
       Employee savedEmployee = employeeRepository.save(testEmployee);

       webTestClient
               .post()
               .uri("/employees")
               .bodyValue(testEmployeeDto)
               .exchange()
               .expectStatus().is5xxServerError();
   }

   @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee(){
         webTestClient
                 .post()
                 .uri("/employees")
                 .bodyValue(testEmployeeDto)
                 .exchange()
                 .expectStatus().isCreated()
                 .expectBody()
                 .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                 .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());

   }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.put()
                .uri("/employees/999")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Random Name");
        testEmployeeDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Random Name");
        testEmployeeDto.setSalary(250L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName())
                .jsonPath("$.salary").isEqualTo(testEmployeeDto.getSalary());

    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

}