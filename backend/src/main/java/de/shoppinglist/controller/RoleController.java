package de.shoppinglist.controller;

import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.dto.RoleDTO;
import de.shoppinglist.entity.Role;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller-Class providing the REST-Endpoints for the Role-Entity
 */
@RestController
@RequestMapping("role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;
    private final ModelMapperDTO modelMapperDTO;

    @Autowired
    public RoleController(RoleService roleService, ModelMapperDTO modelMapperDTO) {
        this.roleService = roleService;
        this.modelMapperDTO = modelMapperDTO;
    }

    @Operation(summary = "Get all role", description = "Get all role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Role.class)))
            })
    })
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.findAll();

        return ResponseEntity.ok(this.modelMapperDTO.mapList(roles, RoleDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        Role role = roleService.findById(id);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(role, RoleDTO.class));
    }

    @Operation(summary = "Create new role", description = "Create new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid role")
    })
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody Role role) {
        Role createdRole = roleService.save(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.modelMapperDTO.getModelMapper().map(createdRole, RoleDTO.class));
    }

    @Operation(summary = "Update one role", description = "Update one role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid role"),
            @ApiResponse(responseCode = "404", description = "role not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        Role role = roleService.update(id, roleDetails);

        return ResponseEntity.ok(this.modelMapperDTO.getModelMapper().map(role, RoleDTO.class));
    }

    @Operation(summary = "Delete one role", description = "Delete one role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))}),
            @ApiResponse(responseCode = "404", description = "role not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

