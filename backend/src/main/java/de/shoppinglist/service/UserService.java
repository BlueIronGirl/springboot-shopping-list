package de.shoppinglist.service;

import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.RoleName;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.repository.ArtikelArchivRepository;
import de.shoppinglist.repository.ArtikelRepository;
import de.shoppinglist.repository.EinkaufszettelRepository;
import de.shoppinglist.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service-Class providing the business logic for the User-Entity
 * <p>
 * The User-Table is used to store the users of the application
 * <p>
 * Users can be registered and logged in
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EinkaufszettelRepository einkaufszettelRepository;
    private final ArtikelRepository artikelRepository;
    private final ArtikelArchivRepository artikelArchivRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, EinkaufszettelRepository einkaufszettelRepository, ArtikelRepository artikelRepository, ArtikelArchivRepository artikelArchivRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.einkaufszettelRepository = einkaufszettelRepository;
        this.artikelRepository = artikelRepository;
        this.artikelArchivRepository = artikelArchivRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllFriends() {
        return findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN) || role.getName().equals(RoleName.ROLE_USER)))
                .toList();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User nicht gefunden"));
    }

    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User nicht gefunden"));

        for (Einkaufszettel einkaufszettel : user.getEinkaufszettelsOwner()) {
            // User aus Owner und SharedWith rausfiltern
            einkaufszettel.setOwners(einkaufszettel.getOwners().stream().filter(owner -> !owner.getId().equals(user.getId())).collect(Collectors.toList()));
            einkaufszettel.setSharedWith(einkaufszettel.getSharedWith().stream().filter(owner -> !owner.getId().equals(user.getId())).collect(Collectors.toList()));

            if (einkaufszettel.getOwners().isEmpty() && einkaufszettel.getSharedWith().isEmpty()) {
                deleteFullEinkaufszettel(einkaufszettel);
            } else {
                if (einkaufszettel.getOwners().isEmpty()) { // wenn es keinen Owner mehr gibt aber noch Shared-With -> Shared-With wird Owner
                    einkaufszettel.setOwners(einkaufszettel.getSharedWith());
                }
                einkaufszettelRepository.save(einkaufszettel);
            }
        }

        userRepository.deleteById(id);
    }

    private void deleteFullEinkaufszettel(Einkaufszettel einkaufszettel) {
        einkaufszettel.getArtikels().forEach(artikel -> artikelRepository.deleteById(artikel.getId()));
        einkaufszettel.getArtikelsArchiv().forEach(artikelArchiv -> artikelArchivRepository.deleteById(artikelArchiv.getId()));
        einkaufszettelRepository.deleteById(einkaufszettel.getId());
    }

    public User update(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDetails.getUsername());
                    if (StringUtils.isNotEmpty(userDetails.getPassword())) {
                        existingUser.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDetails.getPassword())));
                    }
                    existingUser.setName(userDetails.getName());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setRoles(userDetails.getRoles());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new EntityNotFoundException("User nicht gefunden"));
    }
}
