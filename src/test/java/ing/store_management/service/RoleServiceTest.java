package ing.store_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ing.store_management.exception.RoleException;
import ing.store_management.model.dto.RoleDto;
import ing.store_management.model.entity.Role;
import ing.store_management.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private RoleDto roleDto;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up test data
        roleDto = RoleDto.builder()
                .name("Admin")
                .build();

        role = Role.builder()
                .id(1L)
                .name("Admin")
                .build();
    }

    @Test
    void addRole_validRole_roleAdded() {
        //arrange
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        //act
        RoleDto result = roleService.addRole(roleDto);

        //assert
        assertNotNull(result);
        assertEquals(roleDto.getName(), result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void viewRoles_rolesExist_rolesRetrieved() {
        //arrange
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        when(roleRepository.findAll()).thenReturn(roles);

        //act
        List<RoleDto> result = roleService.viewRoles();

        //assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleDto.getName(), result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void deleteRole_roleExists_roleDeleted() {
        //arrange
        when(roleRepository.findByName(role.getName())).thenReturn(role);

        //act
        roleService.deleteRole(role.getName());

        //assert
        verify(roleRepository, times(1)).deleteByName(role.getName());
    }

    @Test
    void deleteRole_roleDoesNotExist_throwRoleException() {
        //arrange
        when(roleRepository.findByName(anyString())).thenReturn(null);

        //act & assert
        assertThrows(RoleException.class, () -> roleService.deleteRole("NonExistingRole"));
        verify(roleRepository, never()).deleteByName(anyString());
    }
}
