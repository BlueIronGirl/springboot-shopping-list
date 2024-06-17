package de.shoppinglist.repository;

import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the User-Entity
 */
public interface EinkaufszettelRepository extends JpaRepository<Einkaufszettel, Long> {
    @Query("select zettel from Einkaufszettel zettel " +
            "where zettel.geloescht = false " +
            "and (?1 MEMBER OF zettel.owners " +
            "or ?2 MEMBER OF zettel.sharedWith)"
    )
    List<Einkaufszettel> findByGeloeschtFalseAndOwners_IdOrSharedWith_Id(User owner, User sharedWith);

    @Query("select zettel from Einkaufszettel zettel " +
            "where zettel.id = ?1 " +
            "and zettel.geloescht = false " +
            "and (?2 MEMBER OF zettel.owners " +
            "or ?3 MEMBER OF zettel.sharedWith)"
    )
    Optional<Einkaufszettel> findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(Long einkaufszettelId, User owner, User sharedWith);

    @Query("select zettel from Einkaufszettel zettel " +
            "where zettel.id = ?1 " +
            "and (?2 MEMBER OF zettel.owners " +
            "or ?3 MEMBER OF zettel.sharedWith)"
    )
    Optional<Einkaufszettel> findByIdAndOwners_IdOrSharedWith_Id(Long einkaufszettelId, User owner, User sharedWith);
}
