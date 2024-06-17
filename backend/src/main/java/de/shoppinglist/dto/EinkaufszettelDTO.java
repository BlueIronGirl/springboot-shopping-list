package de.shoppinglist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EinkaufszettelDTO {
    private Long id;
    private String name;
    private List<ArtikelDTO> artikels;
    private List<UserDTO> owners;
    private List<UserDTO> sharedWith;
    private boolean geloescht;
}
