package com.dalibormucak.im.springrestapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
class ProgrammaticallyValidatingService {

    private final Validator validator;

    @Autowired
    ProgrammaticallyValidatingService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validateObject(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
