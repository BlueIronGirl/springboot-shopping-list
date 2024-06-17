package de.shoppinglist.entity;

import de.shoppinglist.entity.base.EntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Role extends EntityBase {
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Builder
    public Role(Long id, RoleName name) {
        super(id);
        this.name = name;
    }
}
