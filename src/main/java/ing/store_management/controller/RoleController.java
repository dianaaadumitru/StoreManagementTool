package ing.store_management.controller;

import ing.store_management.model.dto.RoleDto;
import ing.store_management.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.addRole(roleDto));
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.viewRoles());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> removeRole(@PathVariable String name) {
        roleService.deleteRole(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
