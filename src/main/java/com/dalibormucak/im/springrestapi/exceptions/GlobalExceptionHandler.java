package com.dalibormucak.im.springrestapi.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import org.postgresql.util.PSQLException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_DB_ERROR_MSG = "Database error";
    private static final String DEFAULT_JSON_ERROR_MSG = "JSON processing error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<?> noSuchElementException(NoSuchElementException ex) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({JsonPatchException.class, JsonProcessingException.class})
    protected ResponseEntity<?> jsonExceptions(Exception ex) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = new ArrayList<>();
        errors.add(DEFAULT_JSON_ERROR_MSG);
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PSQLException.class)
    protected ResponseEntity<?> databaseException(PSQLException ex) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = new ArrayList<>();
        errors.add(DEFAULT_DB_ERROR_MSG);
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> constraintViolationException(ConstraintViolationException ex) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

