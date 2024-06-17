package de.shoppinglist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shoppinglist.entity.base.EntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity-Class representing the ArtikelArchiv-Table
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "artikelarchiv")
public class ArtikelArchiv extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @ManyToMany
    @ToString.Exclude
    private List<Kategorie> kategories;

    private int anzahl;

    private LocalDateTime erstellungsZeitpunkt;

    private LocalDateTime kaufZeitpunkt;

    @ManyToOne
    @JoinColumn(name = "einkaufszettel_id", nullable = false)
    private Einkaufszettel einkaufszettel;

    @ManyToOne
    @JoinColumn(name = "kaeufer_id")
    private User kaeufer;

    public ArtikelArchiv(Artikel artikel) {
        this(artikel.getId(), artikel.getName(), artikel.getKategories(), artikel.getAnzahl(), artikel.getErstellungsZeitpunkt(), artikel.getKaufZeitpunkt(), artikel.getEinkaufszettel(), artikel.getKaeufer());
    }

    @Builder
    public ArtikelArchiv(Long id, Long id1, String name, List<Kategorie> kategories, int anzahl, LocalDateTime erstellungsZeitpunkt, LocalDateTime kaufZeitpunkt) {
        super(id);
        this.id = id1;
        this.name = name;
        this.kategories = kategories;
        this.anzahl = anzahl;
        this.erstellungsZeitpunkt = erstellungsZeitpunkt;
        this.kaufZeitpunkt = kaufZeitpunkt;
    }
}
