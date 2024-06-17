package de.shoppinglist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shoppinglist.entity.base.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity-Class representing the Artikel-Table
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Artikel extends EntityBase {
    @NotBlank
    private String name;

    @ManyToMany
    @ToString.Exclude
    private List<Kategorie> kategories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "einkaufszettel_id", nullable = false)
    @JsonIgnore
    private Einkaufszettel einkaufszettel;

    @Min(1)
    private int anzahl;

    private boolean gekauft;

    private LocalDateTime erstellungsZeitpunkt;

    private LocalDateTime kaufZeitpunkt;

    @ManyToOne
    @JoinColumn(name = "kaeufer_id")
    private User kaeufer;

    @Builder
    public Artikel(Long id, String name, List<Kategorie> kategories, int anzahl, boolean gekauft, LocalDateTime erstellungsZeitpunkt, LocalDateTime kaufZeitpunkt) {
        super(id);
        this.name = name;
        this.kategories = kategories;
        this.anzahl = anzahl;
        this.gekauft = gekauft;
        this.erstellungsZeitpunkt = erstellungsZeitpunkt;
        this.kaufZeitpunkt = kaufZeitpunkt;
    }
}
