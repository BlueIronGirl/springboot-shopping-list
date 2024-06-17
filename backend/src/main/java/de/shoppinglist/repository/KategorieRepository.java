package de.shoppinglist.repository;

import de.shoppinglist.entity.Kategorie;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the Kategorie-Entity
 */
public interface KategorieRepository extends JpaRepository<Kategorie, Long> {

}
