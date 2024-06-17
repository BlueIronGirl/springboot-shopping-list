package de.shoppinglist.service;

import de.shoppinglist.entity.Kategorie;
import de.shoppinglist.repository.KategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service-Class providing the business logic for the Kategorie-Entity
 * <p>
 * The Kategorie-Table is used to display the categories of the articles
 */
@Service
public class KategorieService {
    private final KategorieRepository kategorieRepository;

    @Autowired
    public KategorieService(KategorieRepository kategorieRepository) {
        this.kategorieRepository = kategorieRepository;
    }

    public List<Kategorie> selectAllKategorien() {
        return kategorieRepository.findAll();
    }
}
