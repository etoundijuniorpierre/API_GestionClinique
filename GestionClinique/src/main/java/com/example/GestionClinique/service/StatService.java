package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.stats.StatDuJour;
import com.example.GestionClinique.model.entity.stats.StatMoisDernier;
import com.example.GestionClinique.model.entity.stats.StatMoisEncours;
import com.example.GestionClinique.model.entity.stats.StatsSurLannee;

import java.time.LocalDate;

public interface StatService {
    StatDuJour getOrCreateStatDuJour(LocalDate date);
    StatMoisDernier getOrCreateStatMoisDernier();
    StatMoisEncours getOrCreateStatMoisEncours();
    StatsSurLannee getOrCreateStatsSurLannee(int year);

    StatDuJour calculateAndSaveStatDuJour(LocalDate date);
    StatMoisDernier calculateAndSaveStatMoisDernier();
    StatMoisEncours calculateAndSaveStatMoisEncours();
    StatsSurLannee calculateAndSaveStatsSurLannee(int year);
}