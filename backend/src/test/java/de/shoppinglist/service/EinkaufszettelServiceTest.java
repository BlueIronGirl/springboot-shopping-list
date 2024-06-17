package de.shoppinglist.service;

import de.shoppinglist.entity.Artikel;
import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.User;
import de.shoppinglist.repository.ArtikelRepository;
import de.shoppinglist.repository.EinkaufszettelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author alice_b
 */
@ExtendWith(MockitoExtension.class)
class EinkaufszettelServiceTest {

    @Mock
    private EinkaufszettelRepository einkaufszettelRepository;
    @Mock
    private ArtikelRepository artikelRepository;
    @Mock
    private UserAuthenticationService userAuthenticationService;

    @InjectMocks
    private EinkaufszettelService einkaufszettelService;

    private User user;
    private Einkaufszettel einkaufszettel;
    private Artikel artikel;

    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String NAME = "NAME";
    public static final String TOKEN = "TOKEN";

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

        einkaufszettel = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(new ArrayList<>())
                .build();

        artikel = Artikel.builder()
                .id(1L)
                .build();
    }

    @Test
    void saveEinkaufszettel_givenNotExistingEinkaufszettelWithNoOwner_thenSaveAndReturnEinkaufszettelAndAddOwner() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);

        Einkaufszettel einkaufszettel = Mockito.spy(Einkaufszettel.builder().owners(new ArrayList<>()).build());

        einkaufszettelService.saveEinkaufszettel(einkaufszettel);

        assertEquals(USERNAME, einkaufszettel.getOwners().get(0).getUsername());
        verify(einkaufszettel, times(1)).setOwners(Mockito.any());
        verify(einkaufszettelRepository).save(einkaufszettel);
    }

    @Test
    void saveEinkaufszettel_givenNotExistingEinkaufszettelWithOwner_thenSaveAndReturnEinkaufszettelAndDontAddOwner() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);

        Einkaufszettel einkaufszettel = Mockito.spy(Einkaufszettel.builder().owners(List.of(User.builder().build())).build());

        einkaufszettelService.saveEinkaufszettel(einkaufszettel);

        assertTrue(einkaufszettel.getOwners().size() == 1);
        verify(einkaufszettel, times(0)).setOwners(Mockito.any());
        verify(einkaufszettelRepository).save(einkaufszettel);
    }

    @Test
    void updateEinkaufszettel_givenNotExistingEinkaufszettel_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(null);

        Einkaufszettel einkaufszettel = Einkaufszettel.builder().owners(new ArrayList<>()).build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettel));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUser_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(1L)).thenReturn(Optional.ofNullable(einkaufszettel));

        einkaufszettel.getOwners().add(user);
        Einkaufszettel einkaufszettelData = Mockito.spy(Einkaufszettel.builder().id(1L).owners(List.of(user)).build());
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        verify(einkaufszettelRepository).save(this.einkaufszettel);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndNoOwnerIsDefined_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder().id(1L).owners(new ArrayList<>()).build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUserAndAllOwnersAreRemoved_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        einkaufszettel.getOwners().add(user);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(new ArrayList<>())
                .build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUserAndSharedUserIsAdded_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        einkaufszettel.getOwners().add(user);
        User anotherUser = User.builder().id(2L).build();

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(List.of(anotherUser))
                .owners(List.of(user))
                .build();
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        assertTrue(result.getSharedWith().contains(anotherUser));
        verify(einkaufszettelRepository).save(einkaufszettelData);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUserAndSharedUserIsRemoved_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        einkaufszettel.getOwners().add(user);
        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getSharedWith().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(user))
                .build();
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        assertTrue(result.getSharedWith().isEmpty());
        verify(einkaufszettelRepository).save(einkaufszettelData);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUserAndOwnerIsAdded_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getOwners().add(user);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(user, anotherUser))
                .build();
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        assertTrue(result.getOwners().contains(user));
        assertTrue(result.getOwners().contains(anotherUser));
        verify(einkaufszettelRepository).save(einkaufszettelData);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndOwnerIsCurrentUserAndOwnerIsRemoved_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getOwners().add(user);
        einkaufszettel.getOwners().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(user))
                .build();
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        assertTrue(result.getOwners().contains(user));
        assertFalse(result.getOwners().contains(anotherUser));
        verify(einkaufszettelRepository).save(einkaufszettelData);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndSharedWithIsCurrentUserAndSharedUserIsAdded_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getSharedWith().add(user);
        einkaufszettel.getOwners().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(List.of(user, anotherUser))
                .owners(List.of(anotherUser))
                .build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndSharedWithIsCurrentUserAndOtherSharedUserIsRemoved_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getSharedWith().add(anotherUser);
        einkaufszettel.getSharedWith().add(user);
        einkaufszettel.getOwners().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(List.of(user))
                .owners(List.of(anotherUser))
                .build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndSharedWithIsCurrentUserAndMeAsSharedUserIsRemoved_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getSharedWith().add(user);
        einkaufszettel.getOwners().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(anotherUser))
                .build();
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        Einkaufszettel result = einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData);

        assertEquals(this.einkaufszettel.getId(), result.getId());
        assertTrue(result.getOwners().contains(anotherUser));
        assertFalse(result.getSharedWith().contains(user));
        verify(einkaufszettelRepository).save(einkaufszettelData);
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndSharedWithIsCurrentUserAndOwnerIsAdded_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        einkaufszettel.getSharedWith().add(user);
        einkaufszettel.getOwners().add(anotherUser);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(anotherUser, user))
                .build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void updateEinkaufszettel_givenExistingEinkaufszettelAndSharedWithIsCurrentUserAndOwnerIsRemoved_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        User anotherUser = User.builder().id(2L).build();
        User anotherUser2 = User.builder().id(3L).build();
        einkaufszettel.getSharedWith().add(user);
        einkaufszettel.getOwners().add(anotherUser);
        einkaufszettel.getOwners().add(anotherUser2);

        Einkaufszettel einkaufszettelData = Einkaufszettel.builder()
                .id(1L)
                .sharedWith(new ArrayList<>())
                .owners(List.of(anotherUser))
                .build();

        assertThrows(RuntimeException.class, () -> einkaufszettelService.updateEinkaufszettel(1L, einkaufszettelData));
    }

    @Test
    void deleteEinkaufszettel_givenNotExistingEinkaufszettel_thenThrowException() {
        when(einkaufszettelRepository.findById(Mockito.any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> einkaufszettelService.deleteEinkaufszettel(1L));
        verify(einkaufszettelRepository, Mockito.times(0)).save(this.einkaufszettel);
    }

    @Test
    void deleteEinkaufszettel_givenExistingEinkaufszettel_thenSaveAndReturnEinkaufszettel() {
        when(einkaufszettelRepository.findById(1L)).thenReturn(Optional.ofNullable(einkaufszettel));

        Einkaufszettel einkaufszettelData = Mockito.spy(Einkaufszettel.builder().id(1L).owners(List.of(user)).build());
        when(einkaufszettelRepository.save(einkaufszettel)).thenReturn(einkaufszettelData);

        einkaufszettelService.deleteEinkaufszettel(1L);

        assertTrue(einkaufszettel.isGeloescht());
        verify(einkaufszettelRepository).save(this.einkaufszettel);
    }

    @Test
    void createArtikel_givenNotExistingEinkaufszettel_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.ofNullable(null));

        Artikel artikelData = Mockito.spy(Artikel.builder().id(1L).build());

        assertThrows(RuntimeException.class, () -> einkaufszettelService.createArtikel(1L, artikelData));
        verify(artikelRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void createArtikel_givenExistingEinkaufszettel_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        Artikel artikelData = Mockito.spy(Artikel.builder().id(1L).build());
        when(artikelRepository.save(artikelData)).thenReturn(artikelData);

        einkaufszettelService.createArtikel(1L, artikelData);

        assertEquals(1L, artikelData.getEinkaufszettel().getId());
        verify(artikelRepository).save(artikelData);
    }

    @Test
    void updateArtikel_givenNotExistingArtikel_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        Artikel artikelData = Mockito.spy(Artikel.builder().id(1L).build());

        assertThrows(RuntimeException.class, () -> einkaufszettelService.createArtikel(1L, artikelData));
        verify(artikelRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateArtikel_givenExistingEinkaufszettel_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(artikelRepository.findById(1L)).thenReturn(Optional.ofNullable(artikel));
        when(einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        Artikel artikelData = Mockito.spy(Artikel.builder().id(1L).gekauft(true).build());
        when(artikelRepository.save(Mockito.any())).thenReturn(artikel);

        einkaufszettelService.updateArtikel(1L, 1L, artikelData);

        assertNotNull(artikel.getKaufZeitpunkt());
        verify(artikelRepository).save(artikel);
    }

    @Test
    void deleteArtikel_givenNotExistingArtikel_thenThrowException() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> einkaufszettelService.deleteArtikel(1L, 1L));
        verify(artikelRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteArtikel_givenExistingEinkaufszettel_thenSaveAndReturnEinkaufszettel() {
        when(userAuthenticationService.findCurrentUser()).thenReturn(user);
        when(artikelRepository.findById(1L)).thenReturn(Optional.ofNullable(artikel));
        when(einkaufszettelRepository.findByIdAndGeloeschtFalseAndOwners_IdOrSharedWith_Id(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.ofNullable(einkaufszettel));

        einkaufszettelService.deleteArtikel(1L, 1L);

        assertDoesNotThrow(() -> RuntimeException.class);
        verify(artikelRepository).deleteById(1L);
    }

}
