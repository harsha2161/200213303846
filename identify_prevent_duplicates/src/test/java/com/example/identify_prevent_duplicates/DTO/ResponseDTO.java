package com.example.identify_prevent_duplicates.DTO;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public class ResponseDTO {

    private final HttpStatus status;
    private final String message;
    private final Object data;

}
