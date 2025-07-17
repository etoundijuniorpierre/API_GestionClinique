package com.example.GestionClinique.model.entity.enumElem;

import lombok.Getter;

@Getter
public enum ServiceMedical {
    MEDECINE_GENERALE(5000.0),
    PEDIATRIE(10000.0),
    GYNECOLOGIE(15000.0),
    CARDIOLOGIE(15000.0),
    DERMATOLOGIE(10000.0),
    OPHTALMOLOGIE(5000.0),
    ORTHOPEDIE(5000.0),
    RADIOLOGIE(10000.0),
    LABORATOIRE_ANALYSES(5000.0),
    URGENCES(25000.0),
    KINESITHERAPIE(5000.0),
    DENTISTE(10000.0),
    PSYCHIATRIE(5000.0),
    NEUROLOGIE(15000.0),
    GASTRO_ENTEROLOGIE(10000.0),
    PNEUMOLOGIE(15000.0),
    ENDOCRINOLOGIE(15000.0),
    RHUMATOLOGIE(15000.0);

    private final double montant;

    ServiceMedical(double montant) {
        this.montant = montant;
    }

}
