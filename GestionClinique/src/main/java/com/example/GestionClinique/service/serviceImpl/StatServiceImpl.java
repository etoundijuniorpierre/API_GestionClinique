package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.stats.*;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.StatService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatDuJourRepository statDuJourRepository;
    private final StatsSurLanneeRepository statsSurLanneeRepository;
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final FactureRepository factureRepository;
    private final StatsMoisRepository statsMoisRepository;

    @Override
    @Transactional
    public StatDuJour getOrCreateStatDuJour(LocalDate date) {
        String jourStr = date.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        Optional<StatDuJour> stat = statDuJourRepository.findByJour(jourStr);
        return stat.orElseGet(() -> calculateAndSaveStatDuJour(date));
    }

    @Override
    @Transactional
    public StatsMois getOrCreateStatParMois(int moisNumber) {
        if (moisNumber < 1 || moisNumber > 12) {
            throw new IllegalArgumentException("Numéro de mois invalide. Doit être entre 1 et 12.");
        }
        String moisNom = Month.of(moisNumber).getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase();

        return statsMoisRepository.findByMois(moisNom)
                .orElseGet(() -> {
                    StatsMois newStats = calculateStatsForMonth(moisNumber);
                    return statsMoisRepository.save(newStats);
                });
    }

    @Override
    @Transactional
    public StatsSurLannee getOrCreateStatsSurLannee(int year) {
        String anneeStr = String.valueOf(year);
        Optional<StatsSurLannee> stat = statsSurLanneeRepository.findByAnnee(anneeStr);
        return stat.orElseGet(() -> calculateAndSaveStatsSurLannee(year));
    }

    private StatsMois calculateStatsForMonth(int monthNumber) {
        StatsMois stats = new StatsMois();
        stats.setMois(Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase());
        stats.setNbrRendezVousCONFIRME(rendezVousRepository.countByStatusAndMonth(StatutRDV.CONFIRME, monthNumber));
        stats.setNbrRendezANNULE(rendezVousRepository.countByStatusAndMonth(StatutRDV.ANNULE, monthNumber));
        stats.setNbrPatientEnrg(patientRepository.countByMonth(monthNumber));
        stats.setNbrConsultation(consultationRepository.countByMonth(monthNumber));
        stats.setRevenu(factureRepository.sumRevenueByMonth(monthNumber));
        return stats;
    }


    private StatDuJour calculateAndSaveStatDuJour(LocalDate date) {
        StatDuJour stat = new StatDuJour();
        stat.setJour(date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByJourAndStatut(date, StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByJourAndStatut(date, StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByDateEnregistrement(date));
        stat.setNbrConsultation(consultationRepository.countByDateConsultation(date));
        stat.setRevenu(factureRepository.sumMontantTotalByDateFacture(date));

        return statDuJourRepository.save(stat);
    }

    private StatsSurLannee calculateAndSaveStatsSurLannee(int year) {
        StatsSurLannee stat = new StatsSurLannee();
        stat.setAnnee(String.valueOf(year));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByAnneeAndStatut(year, StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByAnneeAndStatut(year, StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByYearEnregistrement(year));
        stat.setNbrConsultation(consultationRepository.countByYearConsultation(year));
        stat.setRevenu(factureRepository.sumMontantTotalByYearFacture(year));

        return statsSurLanneeRepository.save(stat);
    }
}