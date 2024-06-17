package de.shoppinglist.dto;

import de.shoppinglist.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KategorieDTO {
    private Long id;
    private String name;
}
