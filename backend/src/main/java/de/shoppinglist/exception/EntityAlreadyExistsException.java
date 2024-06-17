package de.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception-Class for the case that an Entity already exists when it should be created
 * resulting in a 409 Conflict
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Das Objekt existiert bereits!")  // 409
public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
