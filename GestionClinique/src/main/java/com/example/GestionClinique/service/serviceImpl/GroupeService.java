package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.GroupeRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final LoggingAspect loggingAspect;
    private final HistoriqueActionService historiqueActionService;

    public Groupe creerGroupe(String nomGroupe, String description, Long idCreateur, List<Long> idsMembres) {

        Utilisateur createur = utilisateurRepository.findById(idCreateur)
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé"));

        List<Utilisateur> membres = utilisateurRepository.findAllById(idsMembres);

        if (!membres.contains(createur)) {
            membres.add(createur);
        }

        Groupe groupe = new Groupe();
        groupe.setNom(nomGroupe);
        groupe.setDescription(description);
        groupe.setCreateur(createur); // Si tu as ce champ, sinon stocke createur en entité
        groupe.setMembres(membres);

        historiqueActionService.enregistrerAction(
                String.format("Groupe créé par l'utilisateur: %s nom du groupe : %s",
                        createur.getNom(), nomGroupe),
                loggingAspect.currentUserId()
        );

        return groupeRepository.save(groupe);
    }

}

