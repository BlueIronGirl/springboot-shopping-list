package de.shoppinglist.service;

import de.shoppinglist.entity.Role;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service-Class providing the business logic for the Role-Entity
 * <p>
 * The Role-Table is used to store the roles of the application
 */
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role nicht gefunden"));
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public void deleteById(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Role nicht gefunden");
        }
    }

    public Role update(Long id, Role roleDetails) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(roleDetails.getName());
                    return roleRepository.save(existingRole);
                })
                .orElseThrow(() -> new EntityNotFoundException("Role nicht gefunden"));
    }
}

