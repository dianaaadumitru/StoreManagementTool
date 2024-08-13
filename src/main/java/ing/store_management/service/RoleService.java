package ing.store_management.service;

import ing.store_management.exception.RoleException;
import ing.store_management.model.dto.RoleDto;
import ing.store_management.model.entity.Role;
import ing.store_management.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleDto addRole(RoleDto newRole) {
        roleRepository.save(Role.builder().name(newRole.getName()).build());
        return newRole;
    }

    public List<RoleDto> viewRoles() {
        Iterable<Role> roesList = roleRepository.findAll();
        List<RoleDto> roleDtos = new ArrayList<>();

        roesList.forEach(role ->
                roleDtos.add(RoleDto.builder()
                        .name(role.getName())
                        .build()));

        return roleDtos;
    }

    public void deleteRole(String name) {
        if (roleRepository.findByName(name) == null) {
            throw new RoleException("rOLE does not exist");
        }
        roleRepository.deleteByName(name);
    }
}
