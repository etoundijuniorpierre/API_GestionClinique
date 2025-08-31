package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "salle")
public class Salle extends BaseEntity {

        @Column(name = "numero", nullable = false, unique = true)
        private String numeroSalle;

        @Enumerated(EnumType.STRING)
        @NotNull // Ensure this is not null
        @Column(name = "service_medical", nullable = false) // Changed to snake_case
        private ServiceMedical serviceMedical;

        @Enumerated(EnumType.STRING) // Ensure EnumType.STRING if you're not already doing it
        @NotNull // Ensure this is not null
        @Column(name = "statut_salle", nullable = false) // Changed to snake_case
        private StatutSalle statutSalle;

        @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private List<RendezVous> rendezVous = new ArrayList<>();

}