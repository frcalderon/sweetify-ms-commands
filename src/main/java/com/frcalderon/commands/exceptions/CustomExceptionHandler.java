package com.frcalderon.commands.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleCommandNotFoundException(CommandNotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        CustomException exception = CustomException.builder()
                .message(e.getMessage())
                .httpStatus(httpStatus)
                .timestamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();

        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleCommandCouldNotBeUpdatedException(CommandCouldNotBeUpdatedException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        CustomException exception = CustomException.builder()
                .message(e.getMessage())
                .httpStatus(httpStatus)
                .timestamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();

        return new ResponseEntity<>(exception, httpStatus);
    }
}
