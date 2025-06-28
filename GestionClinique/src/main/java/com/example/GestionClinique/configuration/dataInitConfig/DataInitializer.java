package com.example.GestionClinique.configuration.dataInitConfig;


import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.repository.RoleRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static com.example.GestionClinique.model.entity.enumElem.RoleType.ADMIN;
import static com.example.GestionClinique.model.entity.enumElem.StatutSalle.DISPONIBLE;

@Configuration
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final SalleRepository salleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(RoleRepository roleRepository,
                           SalleRepository salleRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.salleRepository = salleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initializeDefaultADMIN(PasswordEncoder passwordEncoder,
                                                    RoleRepository roleRepository, // Assurez-vous d'injecter RoleRepository
                                                    UtilisateurRepository utilisateurRepository) { // Assurez-vous d'injecter UtilisateurRepository
        return args -> {
            // Vérifier si la base de données est vide, et si l'utilisateur admin n'existe pas déjà
            if (utilisateurRepository.findAll().isEmpty()) { // Ou mieux: utilisateurRepository.findUtilisateurByInfoPersonnel_Email("admin@gmail.com").isEmpty()
                // 1. Créer ou récupérer le rôle "ADMIN"
                final String ADMIN_ROLE_TYPE = "ADMIN"; // Constante pour éviter les fautes de frappe
                Role adminRole = roleRepository.findFirstByRoleType(RoleType.valueOf(ADMIN_ROLE_TYPE))
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setRoleType(RoleType.valueOf(ADMIN_ROLE_TYPE));
                            // Sauvegarder le nouveau rôle s'il n'existe pas
                            return roleRepository.save(newRole);
                        });

                // 2. Créer l'utilisateur admin
                Utilisateur admin = new Utilisateur();


                admin.setNom("admin");
                admin.setPrenom("admin");
                admin.setEmail("admin@gmail.com");
                admin.setDateNaissance(LocalDate.parse("2001-09-08"));
                admin.setTelephone("+2370061");
                admin.setAdresse("Yaounde Mimboman Sapeur");
                admin.setGenre("M");
                admin.setMotDePasse(passwordEncoder.encode("administrateur"));
                admin.setActif(true);

                // 3. Assigner le rôle "ADMIN" à l'utilisateur
                // Assurez-vous que votre entité Utilisateur a une méthode 'setRoles' qui prend un Set<Role>
                // ou une méthode 'addRole' si vous préférez ajouter un par un.
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRole(roles);

                // 4. Sauvegarder l'utilisateur (ceci devrait aussi sauvegarder la relation ManyToMany)
                utilisateurRepository.save(admin);

                System.out.println("Utilisateur admin par défaut créé avec le rôle ADMIN.");

            } else {
                System.out.println("La base de données contient déjà des utilisateurs. L'admin par défaut n'a pas été créé.");
            }
        };
    }


    @Bean
    public CommandLineRunner initializeSalles() {

        return args -> {
            System.out.println("Starting Salle initialization...");
            int serviceMedicalNumber = 0;
            for(ServiceMedical serviceMedical : ServiceMedical.values()) {
                serviceMedicalNumber++ ;
                ServiceMedical serviceMedicalEnum = ServiceMedical.valueOf(serviceMedical.name());

                if (salleRepository.findByServiceMedical(serviceMedicalEnum).isEmpty()) {
                    Salle salle = new Salle();
                    salle.setNumero("Salle" + serviceMedicalNumber);
                    salle.setServiceMedical(serviceMedicalEnum);
                    salle.setStatutSalle(DISPONIBLE);
                    salleRepository.save(salle);
                    System.out.println("Salle " + salle.getNumero() + " du service médical " + salle.getServiceMedical() + " créée avec succès");
                } else {
                    System.out.println("Salle pour le service médical " + serviceMedicalEnum + " existe déjà. Skipping creation.");
                }

            }

        };
    }

    @Bean
    public CommandLineRunner initializeRoles() {
        return args -> {
            System.out.println("Starting role initialization...");
            // Vérifie si le rôle ADMIN existe, sinon le crée
            if (roleRepository.findByRoleType(ADMIN).isEmpty()) {
                System.out.println("ADMIN role not found, creating...");
                Role adminRole = new Role();
                adminRole.setRoleType(ADMIN);
                try {
                    roleRepository.save(adminRole);
                    System.out.println("Rôle ADMIN créé avec succès !");
                } catch (Exception e) {
                    System.err.println("Error creating ADMIN role: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("ADMIN role already exists.");
            }

            if (roleRepository.findByRoleType(RoleType.MEDECIN).isEmpty()) {
                System.out.println("MEDECIN role not found, creating...");
                Role medecinRole = new Role();
                medecinRole.setRoleType(RoleType.MEDECIN);
                try {
                    roleRepository.save(medecinRole);
                    System.out.println("Rôle MEDECIN créé avec succès !");
                } catch (Exception e) {
                    System.err.println("Error creating MEDECIN role: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("MEDECIN role already exists.");
            }

            if (roleRepository.findByRoleType(RoleType.SECRETAIRE).isEmpty()) {
                System.out.println("SECRETAIRE role not found, creating...");
                Role secretaireRole = new Role();
                secretaireRole.setRoleType(RoleType.SECRETAIRE);
                try {
                    roleRepository.save(secretaireRole);
                    System.out.println("Rôle SECRETAIRE créé avec succès !");
                } catch (Exception e) {
                    System.err.println("Error creating SECRETAIRE role: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("SECRETAIRE role already exists.");
            }
        };
    }
}