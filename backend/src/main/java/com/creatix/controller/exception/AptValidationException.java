package com.creatix.controller.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * exception to be able to provide custom validation error message to frontend.
 *
 * use this exception only in places where you are not able to use simple {@link MethodArgumentNotValidException}
 */
public class AptValidationException extends Exception {

    public AptValidationException(String message) {
        super(message);
    }
}
