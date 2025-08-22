package com.example.GestionClinique.dto.RequestDto.messageRequestDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupeRequestDto {
    private String nom;
    private String description;
    private Long idCreateur;
    private List<Long> idsMembres;
}

