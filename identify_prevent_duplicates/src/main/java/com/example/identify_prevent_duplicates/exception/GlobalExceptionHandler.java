package com.example.identify_prevent_duplicates.exception;

import com.example.identify_prevent_duplicates.DTO.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateNominationException.class)
    public ResponseEntity<ResponseDTO> handleDuplicateNomination(DuplicateNominationException ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO> handleRuntimeException(RuntimeException ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }
}
