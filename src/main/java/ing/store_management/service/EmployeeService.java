package ing.store_management.service;

import ing.store_management.model.dto.EmployeeDto;
import ing.store_management.model.entity.Employee;
import ing.store_management.model.entity.User;
import ing.store_management.model.enums.UserRole;
import ing.store_management.repository.EmployeeRepository;
import ing.store_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeService {
    private final UserRepository userRepository;

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        User user = new User();
        user.setUsername(employeeDto.getUsername());
        user.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        user.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);

        Employee employee = new Employee();
        employee.setEmployeeNumber(employeeDto.getEmployeeNumber());
        employee.setDepartment(employeeDto.getDepartment());
        employee.setUser(savedUser);

        Employee savedEmployee = employeeRepository.save(employee);

        return EmployeeDto.builder()
                .id(savedEmployee.getId())
                .username(savedEmployee.getUser().getUsername())
                .employeeNumber(savedEmployee.getEmployeeNumber())
                .department(savedEmployee.getDepartment())
                .build();
    }

    public List<EmployeeDto> getAllEmployees() {
        Iterable<Employee> employeesList = employeeRepository.findAll();
        List<EmployeeDto> employeeDtos = new ArrayList<>();

        employeesList.forEach(employee ->
                employeeDtos.add(EmployeeDto.builder()
                        .employeeNumber(employee.getEmployeeNumber())
                        .department(employee.getDepartment())
                        .id(employee.getId())
                        .username(employee.getUser().getUsername())
                        .password("secret password")
                        .build()));

        return employeeDtos;
    }
}
