package de.shoppinglist.repository;

import de.shoppinglist.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    List<ConfirmationToken> findByExpiresAtBeforeOrConfirmedAtNotNull(LocalDateTime expiresAt);
}
