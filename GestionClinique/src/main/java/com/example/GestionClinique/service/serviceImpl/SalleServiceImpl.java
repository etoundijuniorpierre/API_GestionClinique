package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.service.SalleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;



@Service
@Transactional
public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;

    @Autowired
    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }


    @Override
    public Salle createSalle(Salle salle) {
        // Check for uniqueness of numero before saving
        if (salleRepository.findByNumeroSalle(salle.getNumeroSalle()).isPresent()) {
            throw new RuntimeException("Salle with number '" + salle.getNumeroSalle() + "' already exists.");
        }
        return salleRepository.save(salle);
    }



    @Transactional
    @Override
    public Salle findSalleById(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle not found with ID: " + id));
    }



    @Override
    @Transactional
    public List<Salle> findAllSalle() {
        return salleRepository.findAll();
    }



    @Override
    public Salle updateSalle(Long id, Salle salleDetails) {
        Salle existingSalle = salleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle not found with ID: " + id));

        if (!salleDetails.getNumeroSalle().equals(existingSalle.getNumeroSalle())) {
            if (salleRepository.findByNumeroSalle(salleDetails.getNumeroSalle()).isPresent()) {
                throw new RuntimeException("Salle with number '" + salleDetails.getNumeroSalle() + "' already exists.");
            }
        }

        existingSalle.setNumeroSalle(salleDetails.getNumeroSalle());
        existingSalle.setServiceMedical(salleDetails.getServiceMedical());
        existingSalle.setStatutSalle(salleDetails.getStatutSalle());

        return salleRepository.save(existingSalle);
    }



    @Override
    public void deleteSalle(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle not found with ID: " + id));
        salleRepository.delete(salle);
    }



    @Override
    @Transactional
    public List<Salle> findSallesByStatut(StatutSalle statutSalle) {
        return salleRepository.findByStatutSalle(statutSalle);
    }

    @Override
    @Transactional
    public Salle findSallesByServiceMedical(ServiceMedical serviceMedical) {
        return salleRepository.findByServiceMedical(serviceMedical);
    }

//    @Override
//    @Transactional
//    public List<Salle> findAvailableSalles(LocalDateTime dateHeureDebut, Long dureeMinutes) {
//        // Calculate dateHeureFin here in the service layer
//        LocalDateTime dateHeureFin = dateHeureDebut.plusMinutes(dureeMinutes);
//        return salleRepository.findAvailableSalles(dateHeureDebut, dateHeureFin);
//    }
}