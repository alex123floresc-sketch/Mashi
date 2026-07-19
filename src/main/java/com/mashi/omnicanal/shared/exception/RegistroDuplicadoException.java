package com.mashi.omnicanal.shared.exception;

import org.springframework.http.HttpStatus;

public class RegistroDuplicadoException extends ApiException {
    public RegistroDuplicadoException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}