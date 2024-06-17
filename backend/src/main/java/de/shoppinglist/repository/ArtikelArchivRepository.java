package de.shoppinglist.repository;

import de.shoppinglist.entity.ArtikelArchiv;
import de.shoppinglist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for the ArtikelArchiv-Entity
 */
public interface ArtikelArchivRepository extends JpaRepository<ArtikelArchiv, Long> {
    @Query("select archiv from ArtikelArchiv archiv " +
            "where ?1 MEMBER OF archiv.einkaufszettel.owners " +
            "or ?2 MEMBER OF archiv.einkaufszettel.sharedWith"
    )
    List<ArtikelArchiv> findByEinkaufszettel_Owners_IdOrEinkaufszettel_SharedWith_IdOrderByKaufZeitpunktDesc(User owner, User sharedWith);

}
