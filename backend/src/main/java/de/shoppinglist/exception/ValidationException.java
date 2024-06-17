package de.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception-Class for the case that an Entity already exists when it should be created
 * resulting in a 409 Conflict
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validierungsfehler!")  // 400
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
