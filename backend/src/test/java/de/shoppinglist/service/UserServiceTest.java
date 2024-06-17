package de.shoppinglist.service;

import de.shoppinglist.entity.Artikel;
import de.shoppinglist.entity.ArtikelArchiv;
import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.User;
import de.shoppinglist.repository.ArtikelArchivRepository;
import de.shoppinglist.repository.ArtikelRepository;
import de.shoppinglist.repository.EinkaufszettelRepository;
import de.shoppinglist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author alice_b
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EinkaufszettelRepository einkaufszettelRepository;

    @Mock
    private ArtikelRepository artikelRepository;

    @Mock
    private ArtikelArchivRepository artikelArchivRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String NAME = "NAME";
    public static final String TOKEN = "TOKEN";

    private User user;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .name(NAME)
                .token(TOKEN)
                .roles(new HashSet<>())
                .build();

        user2 = User.builder()
                .id(2L)
                .username(USERNAME)
                .password(PASSWORD)
                .name(NAME)
                .token(TOKEN)
                .roles(new HashSet<>())
                .build();

        user3 = User.builder()
                .id(3L)
                .username(USERNAME)
                .password(PASSWORD)
                .name(NAME)
                .token(TOKEN)
                .roles(new HashSet<>())
                .build();
    }

    private Einkaufszettel createEinkaufszettel(List<User> owner, List<User> sharedWith) {
        return Einkaufszettel.builder().artikels(new ArrayList<>()).artikelsArchiv(new ArrayList<>()).owners(owner).sharedWith(sharedWith).build();
    }

  /*
  deleteById
   */

    @Test
    void deleteById_givenExistingUserWithEinkaufszettelOnlyOwnedByUserAndNoSharedWith_thenDeleteUserAndFullEinkaufszettel() {
        Einkaufszettel einkaufszettel = createEinkaufszettel(List.of(user), new ArrayList<>());
        Artikel artikel = Artikel.builder().id(1L).build();
        ArtikelArchiv artikelArchiv = ArtikelArchiv.builder().id(1L).build();
        einkaufszettel.setArtikels(List.of(artikel));
        einkaufszettel.setArtikelsArchiv(List.of(artikelArchiv));
        user.setEinkaufszettelsOwner(List.of(einkaufszettel));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
        verify(einkaufszettelRepository, times(1)).deleteById(einkaufszettel.getId());
    }


    @Test
    void deleteById_givenExistingUserWithEinkaufszettelOwnedByUserAndAnotherUserAndNoSharedWith_thenDeleteUserAndChangeOwnerOfEinkaufszettel() {
        Einkaufszettel einkaufszettel = createEinkaufszettel(List.of(user, user2), new ArrayList<>());
        user.setEinkaufszettelsOwner(List.of(einkaufszettel));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        assertFalse(einkaufszettel.getOwners().contains(user));
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(einkaufszettelRepository, times(0)).deleteById(einkaufszettel.getId());
        verify(einkaufszettelRepository, times(1)).save(einkaufszettel);
    }

    @Test
    void deleteById_givenExistingUserWithEinkaufszettelOwnedByUserAndAnotherUserAndSharedWithUser_thenDeleteUserAndChangeOwnerAndSharedWithOfEinkaufszettel() {
        Einkaufszettel einkaufszettel = createEinkaufszettel(List.of(user, user2), List.of(user));
        user.setEinkaufszettelsOwner(List.of(einkaufszettel));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        assertFalse(einkaufszettel.getOwners().contains(user));
        assertFalse(einkaufszettel.getSharedWith().contains(user));
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(einkaufszettelRepository, times(0)).deleteById(einkaufszettel.getId());
        verify(einkaufszettelRepository, times(1)).save(einkaufszettel);
    }

    @Test
    void deleteById_givenExistingUserWithEinkaufszettelOwnedByAnotherUserAndSharedWithUser_thenDeleteUserAndChangeSharedWithOfEinkaufszettel() {
        Einkaufszettel einkaufszettel = createEinkaufszettel(List.of(user2), List.of(user));
        user.setEinkaufszettelsOwner(List.of(einkaufszettel));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        assertFalse(einkaufszettel.getOwners().contains(user));
        assertFalse(einkaufszettel.getSharedWith().contains(user));
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(einkaufszettelRepository, times(0)).deleteById(einkaufszettel.getId());
        verify(einkaufszettelRepository, times(1)).save(einkaufszettel);
    }

    @Test
    void deleteById_givenExistingUserWithEinkaufszettelOwnedUserAndSharedWithAnotherUsers_thenDeleteUserAndChangeOwnerAndSharedWithOfEinkaufszettel() {
        Einkaufszettel einkaufszettel = createEinkaufszettel(List.of(user), List.of(user2, user3));
        user.setEinkaufszettelsOwner(List.of(einkaufszettel));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        assertFalse(einkaufszettel.getOwners().contains(user));
        assertFalse(einkaufszettel.getSharedWith().contains(user));
        assertTrue(einkaufszettel.getOwners().contains(user2));
        assertTrue(einkaufszettel.getOwners().contains(user3));
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(einkaufszettelRepository, times(0)).deleteById(einkaufszettel.getId());
        verify(einkaufszettelRepository, times(1)).save(einkaufszettel);
    }

}
