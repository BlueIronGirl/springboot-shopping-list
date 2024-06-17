package de.shoppinglist.controller;

import de.shoppinglist.dto.KategorieDTO;
import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.entity.Kategorie;
import de.shoppinglist.service.KategorieService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller-Class providing the REST-Endpoints for the Kategorie-Entity
 */
@RestController
@RequestMapping("kategorien")
@PreAuthorize("hasRole('GUEST')")
public class KategorieController {
    private final KategorieService kategorieService;
    private final ModelMapperDTO modelMapperDTO;

    @Autowired
    public KategorieController(KategorieService kategorieService, ModelMapperDTO modelMapperDTO) {
        this.kategorieService = kategorieService;
        this.modelMapperDTO = modelMapperDTO;
    }

    @Operation(summary = "Get all categories", description = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Kategorie.class)))
            })
    })
    @GetMapping
    public ResponseEntity<List<KategorieDTO>> selectAllKategorien() {
        List<Kategorie> kategorieList = kategorieService.selectAllKategorien();

        return ResponseEntity.ok(modelMapperDTO.mapList(kategorieList, KategorieDTO.class));
    }
}
