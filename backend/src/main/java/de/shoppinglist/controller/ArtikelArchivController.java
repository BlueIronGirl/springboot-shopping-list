package de.shoppinglist.controller;

import de.shoppinglist.dto.ArtikelArchivDTO;
import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.entity.Artikel;
import de.shoppinglist.entity.ArtikelArchiv;
import de.shoppinglist.service.ArtikelArchivService;
import de.shoppinglist.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller-Class providing the REST-Endpoints for the ArtikelArchiv-Entity
 */
@RestController
@RequestMapping("archiv")
@PreAuthorize("hasRole('GUEST')")
public class ArtikelArchivController {
    private final ArtikelArchivService artikelArchivService;
    private final ModelMapperDTO modelMapperDTO;

    @Autowired
    public ArtikelArchivController(ArtikelArchivService artikelArchivService, ModelMapperDTO modelMapperDTO) {
        this.artikelArchivService = artikelArchivService;
        this.modelMapperDTO = modelMapperDTO;
    }

    @Operation(summary = "Get all archived articles", description = "Get all archived articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ArtikelArchiv.class)))
            })
    })
    @GetMapping
    public ResponseEntity<List<ArtikelArchivDTO>> selectAllArtikelArchiv() {
        List<ArtikelArchiv> artikelArchivs = artikelArchivService.findByUserId();

        return ResponseEntity.ok(this.modelMapperDTO.mapList(artikelArchivs, ArtikelArchivDTO.class));
    }
}
