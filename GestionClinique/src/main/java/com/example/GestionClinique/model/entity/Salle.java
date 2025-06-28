package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "salle")
public class Salle extends BaseEntity {

        @Column(name = "numero", nullable = false, unique = true) // Ajouté nullable = false et unique = true
        private String numero;

        @Enumerated(EnumType.STRING)
        @Column(name = "serviceMedical", nullable = false) // Ajouté nullable = false
        private ServiceMedical serviceMedical;

        @Column(name = "statutSalle", nullable = false)
        private StatutSalle statutSalle;

        @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté cascade et orphanRemoval, Lazy
        private List<RendezVous> rendezVous = new ArrayList<>(); // Initialisation
}

