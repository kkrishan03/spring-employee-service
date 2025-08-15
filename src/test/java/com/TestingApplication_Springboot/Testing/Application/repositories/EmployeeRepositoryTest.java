package com.TestingApplication_Springboot.Testing.Application.repositories;

import com.TestingApplication_Springboot.Testing.Application.TestContainerConfiguration;
import com.TestingApplication_Springboot.Testing.Application.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainerConfiguration.class)
 @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

   @Autowired
   private  EmployeeRepository employeeRepository;
   private Employee employee;

   @BeforeEach
   void setUp(){
      employee=Employee
              .builder()
              .email("kevalkrishan04@gmail.com")
              .name("keval krishan")
              .salary(1000L).build();
   }

    @Test
    void testFindByEmail_WhenEmailIsValid_ThenReturnEmployee() {
      // Arrange
       employeeRepository.save(employee);

       // ACT
     List<Employee> employees=employeeRepository.findByEmail(employee.getEmail());

     // Assert
     assertThat(employees).isNotNull();
     assertThat(employees).isNotEmpty();
     assertThat(employees.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_WhenEmailIsInvalid_ThenReturnEmptyEmployeeList(){
     // Arrange
     String email="abc@gmail.com";

     // ACT
     List<Employee> employees=employeeRepository.findByEmail(email);

     // Assert
     assertThat(employees).isNotNull();
     assertThat(employees).isEmpty();
    }
}