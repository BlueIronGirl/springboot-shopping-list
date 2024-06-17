package de.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception-Class for the case that a user is not authorized to access a resource
 * resulting in a 401 Unauthorized
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")  // 401
public class UnautorizedException extends RuntimeException {

    public UnautorizedException(String message) {
        super(message);
    }
}
