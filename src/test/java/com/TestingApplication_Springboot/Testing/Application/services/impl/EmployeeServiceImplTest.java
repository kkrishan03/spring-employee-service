package com.TestingApplication_Springboot.Testing.Application.services.impl;

import com.TestingApplication_Springboot.Testing.Application.TestContainerConfiguration;
import com.TestingApplication_Springboot.Testing.Application.dto.EmployeeDto;
import com.TestingApplication_Springboot.Testing.Application.entities.Employee;
import com.TestingApplication_Springboot.Testing.Application.exceptions.ResourceNotFoundException;
import com.TestingApplication_Springboot.Testing.Application.repositories.EmployeeRepository;
import com.TestingApplication_Springboot.Testing.Application.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

     @InjectMocks
     private EmployeeServiceImpl employeeService;

     private Employee mockedEmployee;
     private EmployeeDto mockedEmployeeDto;

     @BeforeEach
     void setup(){
         mockedEmployee=Employee.builder()
                 .id(1L)
                 .email("abc@gmail.com")
                 .name("keval")
                 .salary(100L)
                 .build();

         mockedEmployeeDto=modelMapper.map(mockedEmployee,EmployeeDto.class);
     }

     @Test
      void testGetEmployeeById_WhenEmployeeIsPresent_ReturnEmployeeDto(){
         // Arrange
         Long id=mockedEmployee.getId();
         when(employeeRepository.findById(id)).thenReturn(Optional.of(mockedEmployee));

         // Act
         EmployeeDto employeeDto=employeeService.getEmployeeById(id);

         // Assert
         assertThat(employeeDto.getId()).isEqualTo(id);
         assertThat(employeeDto.getEmail()).isEqualTo(mockedEmployee.getEmail());
         verify(employeeRepository ,atLeast(1)).findById(id);

     }

     @Test
     void testGetEmployeeById_whenEmployeeNotPresent_thenThrowException(){
         // Arrange
         Long id=mockedEmployee.getId();
         when(employeeRepository.findById(any())).thenReturn(Optional.empty());

         // Assert and act
         assertThatThrownBy(()-> employeeService.getEmployeeById(id))
                 .isInstanceOf(ResourceNotFoundException.class)
                 .hasMessage("Employee not found with id: 1");

         verify(employeeRepository).findById(1L);
     }

     @Test
    void  testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){
         //  Assign
         when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
         when(employeeRepository.save(any(Employee.class))).thenReturn(mockedEmployee);

         // Act
    EmployeeDto employeeDto=employeeService.createNewEmployee(mockedEmployeeDto);

    // Assert

         assertThat(employeeDto).isNotNull();
         assertThat(employeeDto.getEmail()).isEqualTo(mockedEmployeeDto.getEmail());
         ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
         verify(employeeRepository).save(employeeArgumentCaptor.capture());
         Employee capturedEmployee = employeeArgumentCaptor.getValue();

         assertThat(capturedEmployee.getEmail()).isEqualTo(mockedEmployee.getEmail());


     }

     @Test
     void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException(){
         // Assign
         when(employeeRepository.findByEmail(mockedEmployee.getEmail())).thenReturn(List.of(mockedEmployee));


         // assert and act
         assertThatThrownBy(()->employeeService.createNewEmployee(mockedEmployeeDto))
                 .isInstanceOf(RuntimeException.class)
                 .hasMessage("Employee already exists with email: " + mockedEmployeeDto.getEmail());

         verify(employeeRepository).findByEmail(mockedEmployeeDto.getEmail());
     }


     @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
       // Arrange
       when(employeeRepository.findById(any())).thenReturn(Optional.empty());

       // Act and Assert
         assertThatThrownBy(()->employeeService.updateEmployee(1L,mockedEmployeeDto))
                 .isInstanceOf(ResourceNotFoundException.class)
                 .hasMessage("Employee not found with id: " + 1L);

         verify(employeeRepository).findById(1L);
         verify(employeeRepository,never()).save(any());

     }


     @Test
      void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException(){
         // Arrange
          when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockedEmployee));
          mockedEmployeeDto.setEmail("random@gmail.com");
          mockedEmployeeDto.setName("random");
         // act and assert
         assertThatThrownBy(()->employeeService.updateEmployee(1L,mockedEmployeeDto))
                 .isInstanceOf(RuntimeException.class)
                 .hasMessage("The email of the employee cannot be updated");

         verify(employeeRepository).findById(1L);
         verify(employeeRepository,never()).save(any());
     }

     @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){
         // Arrange
         when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockedEmployee));
         mockedEmployeeDto.setName("random");
         mockedEmployeeDto.setSalary(10L);

         Employee newEmployee=modelMapper.map(mockedEmployeeDto,Employee.class);
         when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

         // Act
         EmployeeDto employeeDto=employeeService.updateEmployee(1L,mockedEmployeeDto);

         // Assert
         assertThat(employeeDto).isEqualTo(mockedEmployeeDto);
         verify(employeeRepository).findById(1L);
         verify(employeeRepository).save(any());
     }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

//        act
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + 1L);

        verify(employeeRepository, never()).deleteById(anyLong());
    }


    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {
//        arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> employeeService.deleteEmployee(1L))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }

}