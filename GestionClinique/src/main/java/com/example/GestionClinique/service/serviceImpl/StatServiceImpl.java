package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.stats.StatDuJour;
import com.example.GestionClinique.model.entity.stats.StatMoisDernier;
import com.example.GestionClinique.model.entity.stats.StatMoisEncours;
import com.example.GestionClinique.model.entity.stats.StatsSurLannee;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.LoggingAspect;
import com.example.GestionClinique.service.StatService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatDuJourRepository statDuJourRepository;
    private final StatMoisDernierRepository statMoisDernierRepository;
    private final StatMoisEncoursRepository statMoisEncoursRepository;
    private final StatsSurLanneeRepository statsSurLanneeRepository;
    private final HistoriqueActionService historiqueActionService;
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final FactureRepository factureRepository;
    private final LoggingAspect loggingAspect;

    @Override
    @Transactional
    public StatDuJour getOrCreateStatDuJour(LocalDate date) {
        String jourStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (jourStr.isEmpty() || jourStr.equals("0") || jourStr == null) {
            jourStr = String.valueOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        Optional<StatDuJour> stat = statDuJourRepository.findByJour(jourStr);
        return stat.orElseGet(() -> calculateStatDuJour(date));
    }

    @Override
    @Transactional
    public StatMoisDernier getOrCreateStatMoisDernier() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String moisDernierStr = lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        Optional<StatMoisDernier> stat = statMoisDernierRepository.findByMoisDernier(moisDernierStr);
        return stat.orElseGet(this::calculateStatMoisDernier);
    }

    @Override
    @Transactional
    public StatMoisEncours getOrCreateStatMoisEncours() {
        YearMonth currentMonth = YearMonth.now();
        String moisEncoursStr = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        Optional<StatMoisEncours> stat = statMoisEncoursRepository.findByMoisEncours(moisEncoursStr);
        return stat.orElseGet(this::calculateStatMoisEncours);
    }

    @Override
    @Transactional
    public StatsSurLannee getOrCreateStatsSurLannee(int year) {
        String anneeStr = String.valueOf(year);
        if (anneeStr.isEmpty() || anneeStr.equals("0") || anneeStr == null) {
            anneeStr = String.valueOf(Year.now());
        }
        Optional<StatsSurLannee> stat = statsSurLanneeRepository.findByAnnee(anneeStr);
        return stat.orElseGet(() -> calculateStatsSurLannee(year));
    }

    @Override
    @Transactional
    public StatDuJour calculateAndSaveStatDuJour(LocalDate date) {
        StatDuJour stat = calculateStatDuJour(date);
        Optional<StatDuJour> existingStat = statDuJourRepository.findByJour(stat.getJour());
        if (existingStat.isPresent()) {
            StatDuJour s = existingStat.get();
            s.setNbrRendezVousCONFIRME(stat.getNbrRendezVousCONFIRME());
            s.setNbrRendezANNULE(stat.getNbrRendezANNULE());
            s.setNbrPatientEnrg(stat.getNbrPatientEnrg());
            s.setNbrConsultation(stat.getNbrConsultation());
            s.setRevenu(stat.getRevenu());
            return statDuJourRepository.save(s);
        } else {
            return statDuJourRepository.save(stat);
        }
    }

    @Override
    @Transactional
    public StatMoisDernier calculateAndSaveStatMoisDernier() {
        StatMoisDernier stat = calculateStatMoisDernier();
        Optional<StatMoisDernier> existingStat = statMoisDernierRepository.findByMoisDernier(stat.getMoisDernier());
        if (existingStat.isPresent()) {
            StatMoisDernier s = existingStat.get();
            s.setNbrRendezVousCONFIRME(stat.getNbrRendezVousCONFIRME());
            s.setNbrRendezANNULE(stat.getNbrRendezANNULE());
            s.setNbrPatientEnrg(stat.getNbrPatientEnrg());
            s.setNbrConsultation(stat.getNbrConsultation());
            s.setRevenu(stat.getRevenu());
            return statMoisDernierRepository.save(s);
        } else {
            return statMoisDernierRepository.save(stat);
        }
    }

    @Override
    @Transactional
    public StatMoisEncours calculateAndSaveStatMoisEncours() {
        StatMoisEncours stat = calculateStatMoisEncours();
        Optional<StatMoisEncours> existingStat = statMoisEncoursRepository.findByMoisEncours(stat.getMoisEncours());
        if (existingStat.isPresent()) {
            StatMoisEncours s = existingStat.get();
            s.setNbrRendezVousCONFIRME(stat.getNbrRendezVousCONFIRME());
            s.setNbrRendezANNULE(stat.getNbrRendezANNULE());
            s.setNbrPatientEnrg(stat.getNbrPatientEnrg());
            s.setNbrConsultation(stat.getNbrConsultation());
            s.setRevenu(stat.getRevenu());
            return statMoisEncoursRepository.save(s);
        } else {
            return statMoisEncoursRepository.save(stat);
        }
    }

    @Override
    @Transactional
    public StatsSurLannee calculateAndSaveStatsSurLannee(int year) {
        StatsSurLannee stat = calculateStatsSurLannee(year);
        Optional<StatsSurLannee> existingStat = statsSurLanneeRepository.findByAnnee(stat.getAnnee());
        if (existingStat.isPresent()) {
            StatsSurLannee s = existingStat.get();
            s.setNbrRendezVousCONFIRME(stat.getNbrRendezVousCONFIRME());
            s.setNbrRendezANNULE(stat.getNbrRendezANNULE());
            s.setNbrPatientEnrg(stat.getNbrPatientEnrg());
            s.setNbrConsultation(stat.getNbrConsultation());
            s.setRevenu(stat.getRevenu());
            return statsSurLanneeRepository.save(s);
        } else {
            return statsSurLanneeRepository.save(stat);
        }
    }

    private StatDuJour calculateStatDuJour(LocalDate date) {
        StatDuJour stat = new StatDuJour();
        stat.setJour(date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByJourAndStatut(date, StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByJourAndStatut(date, StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByDateEnregistrement(date));
        stat.setNbrConsultation(consultationRepository.countByDateConsultation(date));
        stat.setRevenu(factureRepository.sumMontantTotalByDateFacture(date));

        return stat;
    }

    private StatMoisDernier calculateStatMoisDernier() {
        LocalDate today = LocalDate.now();
        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        StatMoisDernier stat = new StatMoisDernier();
        stat.setMoisDernier(lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByMoisAndStatut(lastMonth.getYear(), lastMonth.getMonthValue(), StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByMoisAndStatut(lastMonth.getYear(), lastMonth.getMonthValue(), StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByMonthEnregistrement(lastMonth.getYear(), lastMonth.getMonthValue()));
        stat.setNbrConsultation(consultationRepository.countByMonthConsultation(lastMonth.getYear(), lastMonth.getMonthValue()));
        stat.setRevenu(factureRepository.sumMontantTotalByMonthFacture(lastMonth.getYear(), lastMonth.getMonthValue()));

        return stat;
    }

    private StatMoisEncours calculateStatMoisEncours() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        StatMoisEncours stat = new StatMoisEncours();
        stat.setMoisEncours(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByMoisAndStatut(currentMonth.getYear(), currentMonth.getMonthValue(), StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByMoisAndStatut(currentMonth.getYear(), currentMonth.getMonthValue(), StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByMonthEnregistrement(currentMonth.getYear(), currentMonth.getMonthValue()));
        stat.setNbrConsultation(consultationRepository.countByMonthConsultation(currentMonth.getYear(), currentMonth.getMonthValue()));
        stat.setRevenu(factureRepository.sumMontantTotalByMonthFacture(currentMonth.getYear(), currentMonth.getMonthValue()));

        return stat;
    }

    private StatsSurLannee calculateStatsSurLannee(int year) {
        StatsSurLannee stat = new StatsSurLannee();
        stat.setAnnee(String.valueOf(year));

        stat.setNbrRendezVousCONFIRME(rendezVousRepository.countByAnneeAndStatut(year, StatutRDV.CONFIRME));
        stat.setNbrRendezANNULE(rendezVousRepository.countByAnneeAndStatut(year, StatutRDV.ANNULE));
        stat.setNbrPatientEnrg(patientRepository.countByYearEnregistrement(year));
        stat.setNbrConsultation(consultationRepository.countByYearConsultation(year));
        stat.setRevenu(factureRepository.sumMontantTotalByYearFacture(year));

        return stat;
    }
}