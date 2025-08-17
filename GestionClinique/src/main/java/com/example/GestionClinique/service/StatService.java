package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.stats.*;

import java.time.LocalDate;

public interface StatService {
    StatDuJour getOrCreateStatDuJour(LocalDate date);
    StatsMois getOrCreateStatParMois(int month);
    StatsSurLannee getOrCreateStatsSurLannee(int year);
}