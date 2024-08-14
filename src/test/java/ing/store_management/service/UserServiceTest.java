package ing.store_management.service;

import ing.store_management.model.dto.UserDto;
import ing.store_management.model.entity.Role;
import ing.store_management.model.entity.User;
import ing.store_management.repository.RoleRepository;
import ing.store_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up test data
        userDto = UserDto.builder()
                .id(1L)
                .username("john_doe")
                .password("password123")
                .role("CUSTOMER")
                .build();

        role = Role.builder().name("CUSTOMER").build();

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setPassword("encoded_password");
        user.setRoles(Set.of(role));

    }

    @Test
    void createUser_validUser_userCreated() {
        //arrange
        when(roleRepository.findByName("CUSTOMER")).thenReturn(role);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //act
        UserDto result = userService.createUser(userDto);

        //assert
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getRole(), result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers_usersExist_usersRetrieved() {
        //arrange
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findAll()).thenReturn(users);

        //act
        List<UserDto> result = userService.getAllUsers();

        //assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getUsername(), result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }
}
