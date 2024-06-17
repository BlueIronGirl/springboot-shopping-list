package de.shoppinglist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtikelArchivDTO {
    private Long id;
    private String name;
    private List<KategorieDTO> kategories;
    private int anzahl;
    private LocalDateTime erstellungsZeitpunkt;
    private LocalDateTime kaufZeitpunkt;
    private EinkaufszettelDTO einkaufszettel;
    private UserDTO kaeufer;
}
