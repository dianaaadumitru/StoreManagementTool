package ing.store_management.service;

import ing.store_management.model.dto.CustomerDto;
import ing.store_management.model.entity.Customer;
import ing.store_management.model.entity.User;
import ing.store_management.model.enums.UserRole;
import ing.store_management.repository.CustomerRepository;
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
public class CustomerService {
    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public CustomerDto createCustomer(CustomerDto customerDto) {
        User user = new User();
        user.setUsername(customerDto.getUsername());
        user.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        user.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setAddress(customerDto.getAddress());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setUser(savedUser);

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerDto.builder()
                .id(savedCustomer.getId())
                .username(savedCustomer.getUser().getUsername())
                .address(savedCustomer.getAddress())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .build();
    }

    public List<CustomerDto> getAllCustomers() {
        Iterable<Customer> customersList = customerRepository.findAll();
        List<CustomerDto> customerDtos = new ArrayList<>();

        customersList.forEach(customer ->
                customerDtos.add(CustomerDto.builder()
                        .address(customer.getAddress())
                        .phoneNumber(customer.getPhoneNumber())
                        .id(customer.getId())
                        .username(customer.getUser().getUsername())
                        .password("secret password")
                        .build()));

        return customerDtos;
    }

}
