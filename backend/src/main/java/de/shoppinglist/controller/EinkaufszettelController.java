package de.shoppinglist.controller;

import de.shoppinglist.dto.ArtikelDTO;
import de.shoppinglist.dto.EinkaufszettelDTO;
import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.entity.Artikel;
import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.service.EinkaufszettelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/einkaufszettel")
@PreAuthorize("hasRole('GUEST')")
public class EinkaufszettelController {
    private final EinkaufszettelService einkaufszettelService;
    private final ModelMapperDTO modelMapperDTO;

    public EinkaufszettelController(EinkaufszettelService einkaufszettelService, ModelMapperDTO modelMapperDTO) {
        this.einkaufszettelService = einkaufszettelService;
        this.modelMapperDTO = modelMapperDTO;
    }

    @Operation(summary = "Get all einkaufszettels", description = "Get all einkaufszettels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Einkaufszettel.class)))
            })
    })
    @GetMapping
    public ResponseEntity<List<EinkaufszettelDTO>> selectAllActiveEinkaufszettels() {
        List<Einkaufszettel> einkaufszettels = einkaufszettelService.findActiveEinkaufszettelsByUserId();

        return ResponseEntity.ok(this.modelMapperDTO.mapList(einkaufszettels, EinkaufszettelDTO.class));
    }

    @Operation(summary = "Create new einkaufszettel", description = "Create new einkaufszettel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Einkaufszettel.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid einkaufszettel")
    })
    @PostMapping
    public ResponseEntity<EinkaufszettelDTO> createEinkaufszettel(@RequestBody Einkaufszettel einkaufszettel) {
        Einkaufszettel einkaufszettelSaved = einkaufszettelService.saveEinkaufszettel(einkaufszettel);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(einkaufszettelSaved, EinkaufszettelDTO.class));
    }

    @Operation(summary = "Update one Einkaufszettel", description = "Update one Einkaufszettel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Einkaufszettel.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Einkaufszettel"),
            @ApiResponse(responseCode = "404", description = "Einkaufszettel not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @PutMapping("{einkaufszettelId}")
    public ResponseEntity<EinkaufszettelDTO> updateEinkaufszettel(@PathVariable(name = "einkaufszettelId") Long id, @Valid @RequestBody Einkaufszettel einkaufszettel) {
        Einkaufszettel einkaufszettelSaved = einkaufszettelService.updateEinkaufszettel(id, einkaufszettel);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(einkaufszettelSaved, EinkaufszettelDTO.class));
    }

    @Operation(summary = "Delete one Einkaufszettel", description = "Delete one Einkaufszettel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Einkaufszettel.class))}),
            @ApiResponse(responseCode = "404", description = "Einkaufszettel not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @DeleteMapping("{einkaufszettelId}")
    public ResponseEntity<Void> deleteEinkaufszettel(@PathVariable(name = "einkaufszettelId") Long id) {
        einkaufszettelService.deleteEinkaufszettel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create new article", description = "Create new article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Artikel.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid article")
    })
    @PostMapping("/{einkaufszettelId}/artikel")
    public ResponseEntity<ArtikelDTO> createArtikel(@PathVariable(name = "einkaufszettelId") Long einkaufszettelId, @Valid @RequestBody Artikel artikel) {
        Artikel artikelSaved = einkaufszettelService.createArtikel(einkaufszettelId, artikel);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(artikelSaved, ArtikelDTO.class));
    }

    @Operation(summary = "Update one article", description = "Update one article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Artikel.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid article"),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @PutMapping("/{einkaufszettelId}/artikel/{id}")
    public ResponseEntity<ArtikelDTO> updateArtikel(@PathVariable(name = "einkaufszettelId") Long einkaufszettelId, @PathVariable(name = "id") Long id, @Valid @RequestBody Artikel artikelData) {
        Artikel artikelSaved = einkaufszettelService.updateArtikel(einkaufszettelId, id, artikelData);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(artikelSaved, ArtikelDTO.class));
    }

    @Operation(summary = "Delete one article", description = "Delete one article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Artikel.class))}),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @DeleteMapping("/{einkaufszettelId}/artikel/{id}")
    public ResponseEntity<Void> deleteArtikel(@PathVariable(name = "einkaufszettelId") Long einkaufszettelId, @PathVariable Long id) {
        einkaufszettelService.deleteArtikel(einkaufszettelId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive all articles of einkaufszettel", description = "Archive all articles of einkaufszettel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema()))
            })
    })
    @PostMapping("/{einkaufszettelId}/archiviereGekaufteArtikel")
    public ResponseEntity<Void> archiviereGekaufteArtikel(@PathVariable(name = "einkaufszettelId") Long einkaufszettelId) {
        einkaufszettelService.archiviereGekaufteArtikel(einkaufszettelId);
        return ResponseEntity.noContent().build();
    }

}
