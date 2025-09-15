package org.elsveys.exception;

import jakarta.persistence.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDatabase(PersistenceException e) {
        e.printStackTrace();
        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return Map.of("error", message);
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneral(RuntimeException e) {
        e.printStackTrace();
        return Map.of("error", e.getMessage());
    }
}