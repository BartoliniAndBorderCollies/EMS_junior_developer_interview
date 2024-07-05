package org.klodnicki.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.klodnicki.DTO.Employee.EmployeeDTORequest;
import org.klodnicki.DTO.Employee.EmployeeDTOResponse;
import org.klodnicki.DTO.ResponseDTO;
import org.klodnicki.exception.NotFoundInDatabaseException;
import org.klodnicki.model.Department;
import org.klodnicki.model.Salary;
import org.klodnicki.model.entity.Employee;
import org.klodnicki.repository.EmployeeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private EmployeeService employeeService;
    private EmployeeDTORequest employeeDTORequest;
    @Mock
    private Employee employee;
    @Mock
    private EmployeeDTOResponse employeeDTOResponse;
    private Long existingId;
    private Long nonExistentId;

    public EmployeeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() {
        employeeDTORequest = new EmployeeDTORequest("firstName", "lastName", "email@email.com", Department.DEPARTMENT1,
                new Salary(100.00));
        existingId = 1L;
        nonExistentId = 999L;
    }

    @Test
    public void create_ShouldMapSaveAndMapAgain_WhenEmployeeDTORequestIsGiven() {
        //Arrange
        when(modelMapper.map(employeeDTORequest, Employee.class)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTOResponse.class)).thenReturn(employeeDTOResponse);

        //Act
        EmployeeDTOResponse actualResponse = employeeService.create(employeeDTORequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(employeeDTOResponse, actualResponse);

        // Verify interactions with mocks
        verify(modelMapper).map(employeeDTORequest, Employee.class);
        verify(employeeRepository).save(employee);
        verify(modelMapper).map(employee, EmployeeDTOResponse.class);
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenEmployeeNotExist() {
        //Arrange
        // Mock repository to return empty Optional
        when(employeeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> employeeService.findById(nonExistentId));
    }

    @Test
    public void findById_ShouldReturnEmployeeDTOResponse_WhenEmployeeExists() throws NotFoundInDatabaseException {
        // Arrange
        Employee employee = new Employee();

        when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDTOResponse.class)).thenReturn(employeeDTOResponse);

        // Act
        EmployeeDTOResponse actualResponse = employeeService.findById(existingId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(employeeDTOResponse, actualResponse);

        // Verify interactions with mocks
        verify(employeeRepository).findById(existingId);
        verify(modelMapper).map(employee, EmployeeDTOResponse.class);
    }

    @Test
    public void findAll_ShouldReturnListEmployeeDTO_WhenExist() {
        //Arrange
        List<Employee> employeeList = Arrays.asList(employee);
        List<EmployeeDTOResponse> expected = Arrays.asList(employeeDTOResponse);

        when(employeeRepository.findAll()).thenReturn(employeeList);
        when(modelMapper.map(employee, EmployeeDTOResponse.class)).thenReturn(employeeDTOResponse);

        //Act
        List<EmployeeDTOResponse> actualResponse = employeeService.findAll();

        //Assert
        assertNotNull(actualResponse);
        assertThat(actualResponse).isEqualTo(expected);

        //Verify mocks
        verify(employeeRepository).findAll();
        verify(modelMapper).map(employee, EmployeeDTOResponse.class);
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenGivenIdNotExist() {
        //Arrange
        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> employeeService.update(nonExistentId, employeeDTORequest));
    }

    @Test
    public void update_ShouldGetValuesIfPresentAndSetAndMapAndSave_WhenIdAndEmployeeDTORequestAreGiven() throws NotFoundInDatabaseException {
        //Arrange
        when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTOResponse.class)).thenReturn(employeeDTOResponse);

        //Act
        EmployeeDTOResponse actualResponse = employeeService.update(existingId, employeeDTORequest);

        //Assert
        assertNotNull(actualResponse, "Actual response should not be null");
        assertEquals(employeeDTOResponse, actualResponse);

        //Verify
        verify(employeeRepository).findById(existingId);
        verify(employeeRepository).save(employee);
        verify(modelMapper).map(employee, EmployeeDTOResponse.class);
    }

    @Test
    public void delete_ShouldThrowNotFoundInDatabaseException_WhenEmployeeNotExist() {
        //Arrange
        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> employeeService.delete(nonExistentId));
    }

    @Test
    public void delete_ShouldFindDeleteAndReturnResponseDTO_WhenEmployeeIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        employee.setFirstName("John");
        employee.setLastName("Deere");

        ResponseDTO expected = new ResponseDTO("Employee " + employee.getFirstName() + " " + employee.getLastName() +
                " has been successfully deleted!", HttpStatus.OK);

        when(employeeRepository.findById(existingId)).thenReturn(Optional.ofNullable(employee));

        //Act
        ResponseDTO actualResponse = employeeService.delete(existingId);

        //Assert
        assertNotNull(actualResponse, "Actual response should not be null");
        assertEquals(expected, actualResponse);

        //Verify
        verify(employeeRepository).findById(existingId);
        verify(employeeRepository).delete(employee);
    }


}