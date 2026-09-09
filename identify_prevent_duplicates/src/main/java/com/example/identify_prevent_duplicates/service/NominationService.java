package com.example.identify_prevent_duplicates.service;

import com.example.identify_prevent_duplicates.DTO.NominationDTO;
import com.example.identify_prevent_duplicates.DTO.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface NominationService {

    ResponseEntity<ResponseDTO> addNomination(NominationDTO nominationDTO);

    ResponseEntity<ResponseDTO> cancelNomination(String nominateId);

    ResponseEntity<ResponseDTO> getAllNominations();
}
