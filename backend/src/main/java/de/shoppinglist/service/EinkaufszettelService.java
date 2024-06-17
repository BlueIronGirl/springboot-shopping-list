package de.shoppinglist.service;

import de.shoppinglist.entity.Artikel;
import de.shoppinglist.entity.ArtikelArchiv;
import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.exception.UnautorizedException;
import de.shoppinglist.exception.ValidationException;
import de.shoppinglist.repository.ArtikelArchivRepository;
import de.shoppinglist.repository.ArtikelRepository;
import de.shoppinglist.repository.EinkaufszettelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class EinkaufszettelService {
    private final EinkaufszettelRepository einkaufszettelRepository;
    private final ArtikelRepository artikelRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final ArtikelArchivRepository artikelArchivRepository;

    public EinkaufszettelService(EinkaufszettelRepository einkaufszettelRepository, ArtikelRepository artikelRepository, UserAuthenticationService userAuthenticationService, ArtikelArchivRepository artikelArchivRepository) {
        this.einkaufszettelRepository = einkaufszettelRepository;
        this.artikelRepository = artikelRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.artikelArchivRepository = artikelArchivRepository;
    }

    public List<Einkaufszettel> findActiveEinkaufszettelsByUserId() {
        User currentUserDB = userAuthenticationService.findCurrentUser();

        return einkaufszettelRepository.findByGeloeschtFalseAndOwners_IdOrSharedWith_Id(currentUserDB, currentUserDB);
    }

    public Einkaufszettel saveEinkaufszettel(Einkaufszettel einkaufszettelData) {
        User currentUserDB = userAuthenticationService.findCurrentUser();

        if (einkaufszettelData.getOwners().isEmpty()) {
            List<User> users = einkaufszettelData.getOwners();
            users.add(currentUserDB);
            einkaufszettelData.setOwners(users);
        }

        return this.einkaufszettelRepository.save(einkaufszettelData);
    }

    public Einkaufszettel updateEinkaufszettel(Long id, Einkaufszettel einkaufszettelData) {
        validateEinkaufszettelUserGeaendert(id, einkaufszettelData);

        return einkaufszettelRepository.findById(id)
                .map(einkaufszettel -> {
                    einkaufszettel.setName(einkaufszettelData.getName());
                    einkaufszettel.setOwners(einkaufszettelData.getOwners());
                    einkaufszettel.setSharedWith(einkaufszettelData.getSharedWith());
                    return einkaufszettelRepository.save(einkaufszettel);
                })
                .orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));
    }

    private void validateEinkaufszettelUserGeaendert(Long id, Einkaufszettel einkaufszettelData) {
        User currentUserDB = userAuthenticationService.findCurrentUser();
        Einkaufszettel einkaufszettelDB = einkaufszettelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));
        if (!einkaufszettelDB.getOwners().isEmpty() && !einkaufszettelDB.getOwners().contains(currentUserDB)) {
            boolean someSharedUserRemovedAsAOnlySharedUser = !einkaufszettelDB.getSharedWith().stream().filter(shared -> !shared.equals(currentUserDB)).allMatch(shared -> einkaufszettelData.getSharedWith().contains(shared));
            boolean someSharedUserAddedAsAOnlySharedUser = !new HashSet<>(einkaufszettelDB.getSharedWith()).containsAll(einkaufszettelData.getSharedWith());
            boolean someOwnerRemovedAsAOnlySharedUser = !new HashSet<>(einkaufszettelData.getOwners()).containsAll(einkaufszettelDB.getOwners());
            boolean someOwnerAddedAsAOnlySharedUser = !new HashSet<>(einkaufszettelDB.getOwners()).containsAll(einkaufszettelData.getOwners());

            if (someSharedUserRemovedAsAOnlySharedUser || someSharedUserAddedAsAOnlySharedUser || someOwnerRemovedAsAOnlySharedUser || someOwnerAddedAsAOnlySharedUser) {
                throw new UnautorizedException("Sie haben keine Berechtigung andere Benutzer zu entfernen oder hinzuzufuegen!");
            }
        }

        if (einkaufszettelData.getOwners().isEmpty()) {
            throw new ValidationException("Es muss mindestens ein Besitzer vorhanden sein!");
        }
    }

    public void deleteEinkaufszettel(Long id) {
        einkaufszettelRepository.findById(id).map(einkaufszettel -> {
                    einkaufszettel.setGeloescht(true);
                    return einkaufszettelRepository.save(einkaufszettel);
                })
                .orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));

    }

    public Artikel createArtikel(Long einkaufszettelId, Artikel artikelData) {
        User currentUserDB = userAuthenticationService.findCurrentUser();
        Einkaufszettel einkaufszettel = einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(einkaufszettelId, currentUserDB, currentUserDB)
                .orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));

        artikelData.setEinkaufszettel(einkaufszettel);
        artikelData.setErstellungsZeitpunkt(LocalDateTime.now());

        return artikelRepository.save(artikelData);
    }

    public Artikel updateArtikel(Long einkaufszettelId, Long artikelId, Artikel artikelData) {
        User currentUserDB = userAuthenticationService.findCurrentUser();
        einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(einkaufszettelId, currentUserDB, currentUserDB)
                .orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));

        return artikelRepository.findById(artikelId)
                .map(artikel -> {
                    artikel.setName(artikelData.getName());
                    artikel.setAnzahl(artikelData.getAnzahl());
                    if (!artikel.isGekauft() && artikelData.isGekauft()) {
                        artikel.setKaufZeitpunkt(LocalDateTime.now());
                        artikel.setKaeufer(currentUserDB);
                    }
                    artikel.setGekauft(artikelData.isGekauft());
                    artikel.setKategories(artikelData.getKategories());
                    return artikelRepository.save(artikel);
                })
                .orElseThrow(() -> new EntityNotFoundException("Artikel nicht gefunden"));
    }

    public void deleteArtikel(Long einkaufszettelId, Long artikelId) {
        User currentUserDB = userAuthenticationService.findCurrentUser();

        einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(einkaufszettelId, currentUserDB, currentUserDB)
                .orElseThrow(() -> new EntityNotFoundException("Einkaufszettel nicht gefunden"));

        artikelRepository.findById(artikelId)
                .orElseThrow(() -> new EntityNotFoundException("Artikel nicht gefunden"));

        artikelRepository.deleteById(artikelId);
    }

    public void archiviereGekaufteArtikel(Long einkaufszettelId) {
        User currentUser = userAuthenticationService.findCurrentUser();

        List<Artikel> gekaufteArtikel = artikelRepository.findByGekauftTrueAndEinkaufszettel_Owners_IdOrEinkaufszettel_SharedWith_Id(einkaufszettelId, currentUser, currentUser);

        // Artikel archivieren
        gekaufteArtikel.stream().map(ArtikelArchiv::new).forEach(artikelArchivRepository::saveAndFlush);

        // Artikel aus Einkaufszettel loeschen
        gekaufteArtikel.forEach(artikel -> artikelRepository.deleteById(artikel.getId()));
    }
}
