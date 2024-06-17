package de.shoppinglist.repository;

import de.shoppinglist.entity.Role;
import de.shoppinglist.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);

}
