package org.klodnicki.service;

import lombok.AllArgsConstructor;
import org.klodnicki.dto.employee.EmployeeDTORequest;
import org.klodnicki.dto.employee.EmployeeDTOResponse;
import org.klodnicki.dto.ResponseDTO;
import org.klodnicki.exception.NotFoundInDatabaseException;
import org.klodnicki.model.Department;
import org.klodnicki.model.entity.Employee;
import org.klodnicki.repository.EmployeeRepository;
import org.klodnicki.service.generic.BasicCrudOperations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class EmployeeService implements BasicCrudOperations<EmployeeDTOResponse, EmployeeDTORequest, Long> {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDTOResponse create(EmployeeDTORequest employeeDTO) {
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);

        return modelMapper.map(savedEmployee, EmployeeDTOResponse.class);
    }

    @Override
    public EmployeeDTOResponse findById(Long id) throws NotFoundInDatabaseException {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Employee.class));
        return modelMapper.map(employee, EmployeeDTOResponse.class);
    }

    /**
     * Retrieves all employees and maps them to a list of EmployeeDTOResponse objects.
     *
     * @return A list of EmployeeDTOResponse objects representing all employees.
     */
    @Override
    public List<EmployeeDTOResponse> findAll() {
        List<EmployeeDTOResponse> employeeDTOResponseList = new ArrayList<>();
        Iterable<Employee> employees = employeeRepository.findAll();

        //use Stream and spliterator method in sequential mode(false) to map to DTO and add it straight to the list
        StreamSupport.stream(employees.spliterator(), false)
                .map(employee -> modelMapper.map(employee, EmployeeDTOResponse.class))
                .forEach(employeeDTOResponseList::add);

        return employeeDTOResponseList;
    }

    /**
     * Updates an existing employee's information.
     *
     * @param id The ID of the employee to update.
     * @param employeeDTORequest The DTO containing the updated employee details.
     * @return The updated EmployeeDTOResponse object.
     * @throws NotFoundInDatabaseException if the employee with the given ID is not found.
     */
    @Override
    public EmployeeDTOResponse update(Long id, EmployeeDTORequest employeeDTORequest) throws NotFoundInDatabaseException {
        Employee employeeToBeUpdated = employeeRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Employee.class));

        Optional.ofNullable(employeeDTORequest.getFirstName()).ifPresent(employeeToBeUpdated::setFirstName);
        Optional.ofNullable(employeeDTORequest.getLastName()).ifPresent(employeeToBeUpdated::setLastName);
        Optional.ofNullable(employeeDTORequest.getEmail()).ifPresent(employeeToBeUpdated::setEmail);
        Optional.ofNullable(employeeDTORequest.getDepartment()).ifPresent(employeeToBeUpdated::setDepartment);
        Optional.ofNullable(employeeDTORequest.getSalary()).ifPresent(employeeToBeUpdated::setSalary);

        employeeRepository.save(employeeToBeUpdated);

        return modelMapper.map(employeeToBeUpdated, EmployeeDTOResponse.class);
    }

    @Override
    public ResponseDTO delete(Long id) throws NotFoundInDatabaseException {
        Employee employeeToBeDeleted = employeeRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Employee.class));
        employeeRepository.delete(employeeToBeDeleted);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Employee " + employeeToBeDeleted.getFirstName() + " " + employeeToBeDeleted.getLastName() +
                " has been successfully deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }

    public List<EmployeeDTOResponse> findByName(String lastName) {
        return mapEmployeeListToDTOs(employeeRepository.findByLastName(lastName));
    }

    public List<EmployeeDTOResponse> findBySalaryRange(double from, double to) {
        return mapEmployeeListToDTOs(employeeRepository.findBySalaryRange(from, to));
    }

    public List<EmployeeDTOResponse> findByDepartment(Department department) {
        return mapEmployeeListToDTOs(employeeRepository.findByDepartment(department));
    }

    /**
     * Maps a list of Employee entities to a list of EmployeeDTOResponse objects.
     *
     * @param employees The list of Employee entities to be mapped.
     * @return A list of EmployeeDTOResponse objects.
     */
    private List<EmployeeDTOResponse> mapEmployeeListToDTOs(List<Employee> employees) {
        List<EmployeeDTOResponse> employeeDTOResponseList = new ArrayList<>();

        employees.forEach(employee -> {
            EmployeeDTOResponse employeeDTOResponse = modelMapper.map(employee, EmployeeDTOResponse.class);
            employeeDTOResponseList.add(employeeDTOResponse);
        });

        return employeeDTOResponseList;
    }

}
