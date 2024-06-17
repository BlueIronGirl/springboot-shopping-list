package de.shoppinglist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO-Class for the Login-Request
 */
@Data
public class LoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
