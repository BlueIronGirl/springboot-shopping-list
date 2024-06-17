package de.shoppinglist.controller;

import de.shoppinglist.dto.LoginDto;
import de.shoppinglist.dto.ModelMapperDTO;
import de.shoppinglist.dto.RegisterDto;
import de.shoppinglist.dto.UserDTO;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.EntityAlreadyExistsException;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.exception.UnautorizedException;
import de.shoppinglist.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller-Class providing the REST-Endpoints for the Login and Register of the Users of the Application
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    private final UserAuthenticationService userAuthenticationService;
    private final ModelMapperDTO modelMapperDTO;

    @Autowired
    public AuthController(UserAuthenticationService userAuthenticationService, ModelMapperDTO modelMapperDTO) {
        this.userAuthenticationService = userAuthenticationService;
        this.modelMapperDTO = modelMapperDTO;
    }

    @Operation(summary = "Login to the Application and get a valid token", description = "Login to the Application and get a valid token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "401", description = "Password is not correct",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UnautorizedException.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityNotFoundException.class))})
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginDto loginDto) {
        User user = userAuthenticationService.login(loginDto);
        user.setToken(userAuthenticationService.createToken(user));

        return ResponseEntity.ok(modelMapperDTO.getModelMapper().map(user, UserDTO.class));
    }

    @Operation(summary = "Refresh user token", description = "Renew user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsException.class))})
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<UserDTO> refreshToken(@Valid @RequestBody String token, HttpServletRequest request) {
        userAuthenticationService.validateToken(request, token);

        User user = userAuthenticationService.findCurrentUser();
        user.setToken(userAuthenticationService.createToken(user));

        return ResponseEntity.ok(modelMapperDTO.getModelMapper().map(user, UserDTO.class));
    }

    @Operation(summary = "Register to the Application and get a valid token", description = "Register to the Application and get a valid token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsException.class))})
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDto registerDto) {
        User createdUser = userAuthenticationService.register(registerDto);
        createdUser.setToken(userAuthenticationService.createToken(createdUser));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Confirm your e-mail-adress and get the user role", description = "Confirm your e-mail-adress and get the user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsException.class))})
    })
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
        this.userAuthenticationService.confirmEmailToken(token);
        return ResponseEntity.noContent().build();
    }
}
