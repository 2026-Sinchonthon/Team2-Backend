package org.example.team2backend.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus();

    String getMessage();

    String name();
}
