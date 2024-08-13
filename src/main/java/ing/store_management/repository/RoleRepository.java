package ing.store_management.repository;

import ing.store_management.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    Role deleteByName(String name);
}
