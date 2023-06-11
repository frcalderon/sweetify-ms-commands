package com.frcalderon.commands.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomException {

    private String message;

    private HttpStatus httpStatus;

    private ZonedDateTime timestamp;
}
