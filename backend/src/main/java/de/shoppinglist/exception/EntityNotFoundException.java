package de.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception-Class for the case that an Entity does not exist when it should be updated or deleted
 * resulting in a 404 Not Found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Das Objekt existiert nicht!")  // 404
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
