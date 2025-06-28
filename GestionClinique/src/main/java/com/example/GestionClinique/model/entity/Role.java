package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "roleType", unique = true, nullable = false) // Ajouté unique et nullable
    private RoleType roleType;

    // Pas de relation ManyToOne ici pour un rôle "lookup"
    // Si vous aviez besoin d'une relation ManyToMany inverse, elle irait ici.
}
