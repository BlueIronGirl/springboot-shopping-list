package de.shoppinglist.repository;

import de.shoppinglist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for the User-Entity
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByLastLoggedInNullAndCreatedAtBefore(LocalDateTime createdAt);

    List<User> findByLastLoggedInBeforeAndEinkaufszettelsOwnerEmptyAndEinkaufszettelsSharedWithEmpty(LocalDateTime lastLoggedIn);
}
