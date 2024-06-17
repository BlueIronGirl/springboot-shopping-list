package de.shoppinglist.controller;

import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.dto.UserDTO;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.service.UserAuthenticationService;
import de.shoppinglist.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller-Class providing the REST-Endpoints for the User-Entity
 */
@RestController
@RequestMapping("user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;
    private final ModelMapperDTO modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapperDTO modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Get all users except me", description = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            })
    })
    @GetMapping("/friends")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UserDTO>> selectAllFriends() {
        List<User> users = userService.findAllFriends();

        return ResponseEntity.ok(modelMapper.mapList(users, UserDTO.class));
    }

    @Operation(summary = "Get all user", description = "Get all user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            })
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();

        return ResponseEntity.ok(modelMapper.mapList(users, UserDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);

        return ResponseEntity.ok(modelMapper.getModelMapper().map(user, UserDTO.class));
    }

    @Operation(summary = "Update one user", description = "Update one user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "404", description = "user not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userService.update(id, userDetails);

        return ResponseEntity.ok(modelMapper.getModelMapper().map(user, UserDTO.class));
    }

    @Operation(summary = "Delete one user", description = "Delete one user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "user not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
