package de.shoppinglist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shoppinglist.entity.base.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Entity-Class representing the User-Table
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
public class User extends EntityBase {
    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @Transient
    private String token;

    @ManyToMany(mappedBy = "sharedWith")
    @ToString.Exclude
    @JsonIgnore
    private List<Einkaufszettel> einkaufszettelsSharedWith;

    @ManyToMany(mappedBy = "owners")
    @ToString.Exclude
    @JsonIgnore
    private List<Einkaufszettel> einkaufszettelsOwner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoggedIn;

    @Builder
    public User(Long id, String username, String password, String name, String email, String token, Set<Role> roles, LocalDateTime createdAt, LocalDateTime lastLoggedIn) {
        super(id);
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.token = token;
        this.roles = roles;
        this.createdAt = createdAt;
        this.lastLoggedIn = lastLoggedIn;
    }
}
