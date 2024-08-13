package ing.store_management.service;

import ing.store_management.model.dto.UserDto;
import ing.store_management.model.entity.User;
import ing.store_management.repository.RoleRepository;
import ing.store_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    public UserDto createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName("CUSTOMER")));
        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .role(savedUser.getRoles().iterator().next().getName())
                .build();
    }

    public List<UserDto> getAllUsers() {
        Iterable<User> usersList = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        usersList.forEach(user ->
                userDtos.add(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password("secret password")
                        .role(user.getRoles().iterator().next().getName())
                        .build()));

        return userDtos;
    }
}
