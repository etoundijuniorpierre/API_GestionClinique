// PatientDto.java (updated)
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Patient;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel;
}