package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Consultation;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationSummaryDto {
    private Integer id;
    private String motifs;
    private String diagnostic;
    private Integer rendezVousId;
    private Integer patientId;

}