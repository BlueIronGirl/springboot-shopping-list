package de.shoppinglist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shoppinglist.entity.base.EntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Einkaufszettel extends EntityBase {
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "einkaufszettel")
    @ToString.Exclude
    private List<Artikel> artikels;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "einkaufszettel")
    @ToString.Exclude
    @JsonIgnore
    private List<ArtikelArchiv> artikelsArchiv;

    @ManyToMany
    @JoinTable(
            name = "einkaufszettel_owner",
            joinColumns = @JoinColumn(name = "einkaufszettel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> owners;

    @ManyToMany
    @JoinTable(
            name = "einkaufszettel_user",
            joinColumns = @JoinColumn(name = "einkaufszettel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> sharedWith;

    private boolean geloescht;

    @Builder
    public Einkaufszettel(Long id, String name, List<Artikel> artikels, List<ArtikelArchiv> artikelsArchiv, List<User> owners, List<User> sharedWith, boolean geloescht) {
        super(id);
        this.name = name;
        this.artikels = artikels;
        this.artikelsArchiv = artikelsArchiv;
        this.owners = owners;
        this.sharedWith = sharedWith;
        this.geloescht = geloescht;
    }
}
