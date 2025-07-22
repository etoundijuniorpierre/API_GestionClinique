package com.example.project.aspect; 

import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.authService.SecurityUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

// Importez vos entités et DTOs pertinents
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Prescription;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.HistoriqueAction;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.dto.dtoConnexion.LoginRequest; // Pour le login
import com.example.GestionClinique.service.authService.MonUserDetailsCustom; // Pour le login/logout


@Aspect
@Component
@AllArgsConstructor
public class LoggingAspect {

    private final HistoriqueActionService historiqueActionService;
    private SecurityUtil securityUtil;

    public LoggingAspect(HistoriqueActionService historiqueActionService) {
        this.historiqueActionService = historiqueActionService;

    }

    // --- Pointcuts (où intercepter les méthodes) ---

    @Pointcut("execution(* com.example.GestionClinique.service.*.create*(..)) || " +
              "execution(* com.example.GestionClinique.service.*.update*(..)) || " +
              "execution(* com.example.GestionClinique.service.*.delete*(..)) || " +
              "execution(* com.example.GestionClinique.service.RendezVousServiceImpl.cancelRendezVous(..)) || " +
              "execution(* com.example.GestionClinique.service.ConsultationServiceImpl.startConsultation(..)) || " +
              "execution(* com.example.GestionClinique.service.ConsultationServiceImpl.createConsultation(..)) || " +
              "execution(* com.example.GestionClinique.service.UtilisateurServiceImpl.updateUtilisateurStatus(..)) || " +
              "execution(* com.example.GestionClinique.service.ConsultationServiceImpl.deleteById(..)) || " +
              "execution(* com.example.GestionClinique.service.FactureServiceImpl.generateInvoiceForConsultation(..)) || " +
              "execution(* com.example.GestionClinique.service.FactureServiceImpl.updateFactureStatus(..)) || " +
              "execution(* com.example.GestionClinique.service.PatientServiceImpl.createPatient(..)) || " +
              "execution(* com.example.GestionClinique.service.PatientServiceImpl.updatePatient(..)) || " +
              "execution(* com.example.GestionClinique.service.PatientServiceImpl.deletePatient(..)) || " +
              "execution(* com.example.GestionClinique.service.DossierMedicalServiceImpl.createDossierMedical(..)) || " +
              "execution(* com.example.GestionClinique.service.DossierMedicalServiceImpl.updateDossierMedical(..)) || " +
              "execution(* com.example.GestionClinique.service.DossierMedicalServiceImpl.addConsultationToDossier(..)) || " +
              "execution(* com.example.GestionClinique.service.DossierMedicalServiceImpl.addPrescriptionToDossier(..)) || " +
              "execution(* com.example.GestionClinique.service.DossierMedicalServiceImpl.deleteDossierMedical(..)) || " +
              "execution(* com.example.GestionClinique.service.RoleServiceImpl.createRole(..)) || " +
              "execution(* com.example.GestionClinique.service.RoleServiceImpl.updateRole(..)) || " +
              "execution(* com.example.GestionClinique.service.RoleServiceImpl.deleteRole(..)) || " +
              "execution(* com.example.GestionClinique.service.SalleServiceImpl.createSalle(..)) || " +
              "execution(* com.example.GestionClinique.service.SalleServiceImpl.updateSalle(..)) || " +
              "execution(* com.example.GestionClinique.service.SalleServiceImpl.updateStatutSalle(..)) || " +
              "execution(* com.example.GestionClinique.service.SalleServiceImpl.deleteSalle(..)) || " +
              // Nouveaux Pointcuts pour le login et le logout
              "execution(* com.example.GestionClinique.controller.AuthController.login(..)) || " + // Pour l'authentification réussie
              "execution(* com.example.GestionClinique.service.authService.CustomLogoutHandler.logout(..))" // Pour la déconnexion
    )
    public void dataModificationMethods() {}

    // --- Advice (ce qui se passe quand la méthode est interceptée) ---

    @AfterReturning(pointcut = "dataModificationMethods()", returning = "result")
    public void logSuccessfulAction(JoinPoint joinPoint, Object result) {
        Long userId = null;
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();


        if ("login".equals(methodName) && result instanceof org.springframework.http.ResponseEntity<?> responseEntity) {
            if (responseEntity.getBody() instanceof com.example.GestionClinique.dto.dtoConnexion.LoginResponse loginResponse) {
                userId = loginResponse.getId();
            }
        } else if ("logout".equals(methodName)) {
            // Pour le logout, l'ID est dans l'objet MonUserDetailsCustom dans l'Authentication
            // L'authentication est un des arguments de la méthode logout
            if (joinPoint.getArgs().length > 2 && joinPoint.getArgs()[2] instanceof org.springframework.security.core.Authentication authentication) {
                if (authentication.getPrincipal() instanceof MonUserDetailsCustom userDetails) {
                    userId = userDetails.getId();
                }
            }
        } else {
            // Pour les autres méthodes, utilisez SecurityUtil si disponible
            userId = securityUtil.getCurrentAuthenticatedUserId();
        }


        String description = buildActionDescription(methodName, result, joinPoint.getArgs());

        historiqueActionService.enregistrerAction(
            "Action réussie dans " + className + "." + methodName + ": " + description,
            userId
        );
    }

    @AfterThrowing(pointcut = "dataModificationMethods()", throwing = "e")
    public void logFailedAction(JoinPoint joinPoint, Throwable e) {
        Long userId = null;
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        // Tente de récupérer l'ID de l'utilisateur même en cas d'échec
        if ("login".equals(methodName) && joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof LoginRequest loginRequest) {
            // Pour un échec de login, on n'a pas d'ID d'utilisateur authentifié, juste l'email
           historiqueActionService.enregistrerAction(
                "Échec de l'action dans " + className + "." + methodName + " (email: " + loginRequest.getEmail() + "): " + e.getMessage()
            );
            return;
        } else {
            userId = securityUtil.getCurrentAuthenticatedUserId(); // Pour les autres échecs
        }


        historiqueActionService.enregistrerAction(
            "Échec de l'action dans " + className + "." + methodName + ": " + e.getMessage(),
            userId
        );
    }

    // --- Méthode d'aide pour construire la description ---
    private String buildActionDescription(String methodName, Object result, Object[] args) {
        StringBuilder description = new StringBuilder();

        switch (methodName) {
            // --- UtilisateurServiceImpl ---
            case "createUtilisateur":
                if (result instanceof Utilisateur u) {
                    description.append(String.format("Création utilisateur : %s %s (ID: %d, Email: %s, Rôle: %s).",
                            u.getNom(), u.getPrenom(), u.getId(), u.getEmail(), u.getRole() != null ? u.getRole().getRoleType().name() : "N/A"));
                }
                break;
            case "updateUtilisateur":
                if (result instanceof Utilisateur u) {
                    description.append(String.format("Mise à jour utilisateur (ID: %d, Email: %s).",
                            u.getId(), u.getEmail()));
                }
                break;
            case "deleteUtilisateur":
                if (args.length > 0 && args[0] instanceof Long userIdToDelete) {
                    description.append(String.format("Suppression utilisateur (ID: %d).", userIdToDelete));
                }
                break;
            case "updateUtilisateurStatus":
                if (args.length > 1 && args[0] instanceof Long userIdToUpdate && args[1] instanceof Boolean isActive) {
                    description.append(String.format("Changement de statut utilisateur (ID: %d) à Actif: %b.", userIdToUpdate, isActive));
                }
                break;

            // --- RendezVousServiceImpl ---
            case "createRendezVous":
                if (result instanceof RendezVous rv) {
                    description.append(String.format("Création rendez-vous (ID: %d) pour patient %s (ID: %s) avec médecin %s (ID: %s) le %s à %s. Statut: %s.",
                            rv.getId(), rv.getPatient() != null ? rv.getPatient().getNom() : "N/A", rv.getPatient() != null ? rv.getPatient().getId() : 0,
                            rv.getMedecin() != null ? rv.getMedecin().getNom() : "N/A", rv.getMedecin() != null ? rv.getMedecin().getId() : 0,
                            rv.getJour(), rv.getHeure(), rv.getStatut().name()));
                }
                break;
            case "updateRendezVous":
                if (result instanceof RendezVous rv) {
                    description.append(String.format("Mise à jour rendez-vous (ID: %d) pour patient %s (ID: %s). Nouveau statut: %s.",
                            rv.getId(), rv.getPatient() != null ? rv.getPatient().getNom() : "N/A", rv.getPatient() != null ? rv.getPatient().getId() : 0,
                            rv.getStatut().name()));
                }
                break;
            case "deleteRendezVous":
                if (args.length > 0 && args[0] instanceof Long rendezVousIdToDelete) {
                    description.append(String.format("Suppression rendez-vous (ID: %d).", rendezVousIdToDelete));
                }
                break;
            case "cancelRendezVous":
                if (result instanceof RendezVous rv) {
                    description.append(String.format("Annulation rendez-vous (ID: %d) pour patient %s (ID: %s).",
                            rv.getId(), rv.getPatient() != null ? rv.getPatient().getNom() : "N/A", rv.getPatient() != null ? rv.getPatient().getId() : 0));
                }
                break;

            // --- ConsultationServiceImpl ---
            case "createConsultation": // Emergency consultation
                if (result instanceof Consultation c) {
                    description.append(String.format("Création consultation d'urgence (ID: %d) par médecin %s (ID: %d).",
                            c.getId(), c.getMedecin() != null ? c.getMedecin().getNom() : "N/A", c.getMedecin() != null ? c.getMedecin().getId() : 0));
                }
                break;
            case "startConsultation": // Scheduled consultation
                if (result instanceof Consultation c) {
                    description.append(String.format("Début consultation programmée (ID: %d) via RV (ID: %d) pour patient %s (ID: %d) par médecin %s (ID: %d).",
                            c.getId(), c.getRendezVous() != null ? c.getRendezVous().getId() : 0,
                            c.getDossierMedical() != null && c.getDossierMedical().getPatient() != null ? c.getDossierMedical().getPatient().getNom() : "N/A",
                            c.getDossierMedical() != null && c.getDossierMedical().getPatient() != null ? c.getDossierMedical().getPatient().getId() : 0,
                            c.getMedecin() != null ? c.getMedecin().getNom() : "N/A", c.getMedecin() != null ? c.getMedecin().getId() : 0));
                }
                break;
            case "updateConsultation":
                if (result instanceof Consultation c) {
                    description.append(String.format("Mise à jour consultation (ID: %d) pour patient %s (ID: %s).",
                            c.getId(), c.getDossierMedical() != null && c.getDossierMedical().getPatient() != null ? c.getDossierMedical().getPatient().getNom() : "N/A",
                            c.getDossierMedical() != null && c.getDossierMedical().getPatient() != null ? c.getDossierMedical().getPatient().getId() : 0));
                }
                break;
            case "deleteById": // Specific for ConsultationService.deleteById
                if (args.length > 0 && args[0] instanceof Long consultationIdToDelete) {
                    description.append(String.format("Suppression consultation (ID: %d).", consultationIdToDelete));
                }
                break;
            case "addPrescriptionToConsultation":
                if (result instanceof Prescription p) {
                    description.append(String.format("Ajout prescription (ID: %d) à consultation (ID: %d) pour patient %s (ID: %s).",
                            p.getId(), p.getConsultation() != null ? p.getConsultation().getId() : 0,
                            p.getPatient() != null ? p.getPatient().getNom() : "N/A", p.getPatient() != null ? p.getPatient().getId() : 0));
                }
                break;

            // --- PrescriptionServiceImpl ---
            case "createPrescription":
                if (result instanceof Prescription p) {
                    description.append(String.format("Création prescription (ID: %d) pour patient %s (ID: %s) par médecin %s (ID: %s).",
                            p.getId(), p.getPatient() != null ? p.getPatient().getNom() : "N/A", p.getPatient() != null ? p.getPatient().getId() : 0,
                            p.getMedecin() != null ? p.getMedecin().getNom() : "N/A", p.getMedecin() != null ? p.getMedecin().getId() : 0));
                }
                break;
            case "updatePrescription":
                if (result instanceof Prescription p) {
                    description.append(String.format("Mise à jour prescription (ID: %d) pour patient %s (ID: %s).",
                            p.getId(), p.getPatient() != null ? p.getPatient().getNom() : "N/A", p.getPatient() != null ? p.getPatient().getId() : 0));
                }
                break;
            case "deletePrescription":
                if (args.length > 0 && args[0] instanceof Long prescriptionIdToDelete) {
                    description.append(String.format("Suppression prescription (ID: %d).", prescriptionIdToDelete));
                }
                break;

            // --- FactureServiceImpl ---
            case "generateInvoiceForConsultation":
                if (result instanceof Facture f) {
                    description.append(String.format("Génération facture (ID: %d) pour consultation (ID: %d). Montant: %.2f %s, Statut: %s.",
                            f.getId(), f.getConsultation() != null ? f.getConsultation().getId() : 0,
                            f.getMontant(), f.getStatutPaiement().name()));
                }
                break;
            case "updateFacture":
                if (result instanceof Facture f) {
                    description.append(String.format("Mise à jour facture (ID: %d). Nouveau statut: %s.",
                            f.getId(), f.getStatutPaiement().name()));
                }
                break;
            case "updateFactureStatus":
                if (args.length > 1 && args[0] instanceof Long factureId && args[1] != null) {
                    description.append(String.format("Changement de statut facture (ID: %d) à %s.", factureId, args[1].toString()));
                }
                break;
            case "deleteFacture":
                if (args.length > 0 && args[0] instanceof Long factureIdToDelete) {
                    description.append(String.format("Suppression facture (ID: %d).", factureIdToDelete));
                }
                break;

            // --- PatientServiceImpl ---
            case "createPatient":
                if (result instanceof Patient p) {
                    description.append(String.format("Création patient : %s %s (ID: %d).",
                            p.getNom(), p.getPrenom(), p.getId()));
                }
                break;
            case "updatePatient":
                if (result instanceof Patient p) {
                    description.append(String.format("Mise à jour patient (ID: %d, Nom: %s %s).",
                            p.getId(), p.getNom(), p.getPrenom()));
                }
                break;
            case "deletePatient":
                if (args.length > 0 && args[0] instanceof Long patientIdToDelete) {
                    description.append(String.format("Suppression patient (ID: %d).", patientIdToDelete));
                }
                break;

            // --- DossierMedicalServiceImpl ---
            case "createDossierMedical":
                if (result instanceof DossierMedical dm) {
                    description.append(String.format("Création dossier médical (ID: %d) pour patient %s (ID: %s).",
                            dm.getId(), dm.getPatient() != null ? dm.getPatient().getNom() : "N/A", dm.getPatient() != null ? dm.getPatient().getId() : 0));
                }
                break;
            case "updateDossierMedical":
                if (result instanceof DossierMedical dm) {
                    description.append(String.format("Mise à jour dossier médical (ID: %d) pour patient %s (ID: %s).",
                            dm.getId(), dm.getPatient() != null ? dm.getPatient().getNom() : "N/A", dm.getPatient() != null ? dm.getPatient().getId() : 0));
                }
                break;
            case "addConsultationToDossier":
                if (result instanceof DossierMedical dm && args.length > 1 && args[1] instanceof Long consultationId) {
                    description.append(String.format("Ajout consultation (ID: %d) au dossier médical (ID: %d) du patient %s (ID: %s).",
                            consultationId, dm.getId(), dm.getPatient() != null ? dm.getPatient().getNom() : "N/A", dm.getPatient() != null ? dm.getPatient().getId() : 0));
                }
                break;
            case "addPrescriptionToDossier":
                if (result instanceof DossierMedical dm && args.length > 1 && args[1] instanceof Long prescriptionId) {
                    description.append(String.format("Ajout prescription (ID: %d) au dossier médical (ID: %d) du patient %s (ID: %s).",
                            prescriptionId, dm.getId(), dm.getPatient() != null ? dm.getPatient().getNom() : "N/A", dm.getPatient() != null ? dm.getPatient().getId() : 0));
                }
                break;
            case "deleteDossierMedical":
                if (args.length > 0 && args[0] instanceof Long dmIdToDelete) {
                    description.append(String.format("Suppression dossier médical (ID: %d).", dmIdToDelete));
                }
                break;

            // --- RoleServiceImpl ---
            case "createRole":
                if (result instanceof Role r) {
                    description.append(String.format("Création rôle : %s (ID: %d).",
                            r.getRoleType().name(), r.getId()));
                }
                break;
            case "updateRole":
                if (result instanceof Role r) {
                    description.append(String.format("Mise à jour rôle (ID: %d, Type: %s).",
                            r.getId(), r.getRoleType().name()));
                }
                break;
            case "deleteRole":
                if (args.length > 0 && args[0] instanceof Long roleIdToDelete) {
                    description.append(String.format("Suppression rôle (ID: %d).", roleIdToDelete));
                }
                break;

            // --- SalleServiceImpl ---
            case "createSalle":
                if (result instanceof Salle s) {
                    description.append(String.format("Création salle : %s (ID: %d).",
                            s.getNumeroSalle(), s.getId()));
                }
                break;
            case "updateSalle":
                if (result instanceof Salle s) {
                    description.append(String.format("Mise à jour salle (ID: %d, Nom: %s).",
                            s.getId(), s.getNumeroSalle()));
                }
                break;
            case "updateStatutSalle":
                if (args.length > 1 && args[0] instanceof Long salleId && args[1] != null) {
                    description.append(String.format("Changement de statut salle (ID: %d) à %s.", salleId, args[1].toString()));
                }
                break;
            case "deleteSalle":
                if (args.length > 0 && args[0] instanceof Long salleIdToDelete) {
                    description.append(String.format("Suppression salle (ID: %d).", salleIdToDelete));
                }
                break;

            // --- AUTHENTICATION ---
            case "login":
                // Le résultat pour le login est un ResponseEntity.
                if (result instanceof org.springframework.http.ResponseEntity<?> responseEntity &&
                    responseEntity.getBody() instanceof com.example.GestionClinique.dto.dtoConnexion.LoginResponse loginResponse) {
                    description.append(String.format("Connexion réussie de l'utilisateur : %s (ID: %d).",
                            loginResponse.getUsername(), loginResponse.getId()));
                } else if (args.length > 0 && args[0] instanceof LoginRequest loginRequest) {
                    // Cas où la méthode login a été appelée, mais le 'result' n'est pas ce que l'on attend
                    // (peut-être une erreur interne avant de former la LoginResponse, mais après l'authentification)
                    description.append(String.format("Tentative de connexion de l'utilisateur : %s. Statut de réponse inconnu.",
                            loginRequest.getEmail()));
                }
                break;
            case "logout":
                // Pour le logout, l'action est enregistrée après le retour de la méthode logout.
                // L'objet `result` est `void` pour `logout`, donc on se base sur les `args`.
                // Les arguments de logout sont (HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                if (args.length > 2 && args[2] instanceof org.springframework.security.core.Authentication authentication) {
                    if (authentication.getPrincipal() instanceof MonUserDetailsCustom userDetails) {
                        description.append(String.format("Déconnexion de l'utilisateur : %s (ID: %d).",
                                userDetails.getUsername(), userDetails.getId()));
                    } else if (authentication.getPrincipal() instanceof String principalString) {
                         // Fallback pour les cas où le principal n'est pas MonUserDetailsCustom (ex: anonyme)
                        description.append(String.format("Déconnexion de l'utilisateur : %s (Principal String).", principalString));
                    } else {
                        description.append("Déconnexion d'un utilisateur non identifié ou type de principal inconnu.");
                    }
                } else {
                    description.append("Déconnexion déclenchée sans informations d'authentification claires.");
                }
                break;

            default:
                description.append("Action non spécifiée pour la journalisation détaillée.");
                break;
        }
        return description.toString();
    }
}