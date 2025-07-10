package com.example.GestionClinique.model.entity.enumElem;

import lombok.Getter; // If you want to use Lombok's @Getter for consistency

@Getter // Optional, if you plan to add a custom method later (e.g., getDescription())
public enum StatusConnect {
    CONNECTE,
    DECONNECTE
}