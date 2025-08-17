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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.GestionClinique.model.entity.enumElem.RoleType.*;
import static com.example.GestionClinique.model.entity.enumElem.StatutSalle.DISPONIBLE;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initializeData(RoleRepository roleRepository,
                                            UtilisateurRepository utilisateurRepository,
                                            SalleRepository salleRepository,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Initialize all roles if they don't exist
            initializeRoles(roleRepository);

            // 2. Initialize admin user if none exists
            initializeAdminUser(utilisateurRepository, roleRepository, passwordEncoder);

            // 3. Initialize salles if they don't exist
            initializeSalles(salleRepository);
        };
    }

    private void initializeRoles(RoleRepository roleRepository) {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            Optional<Role> existingRole = roleRepository.findFirstByRoleType(roleType);
            if (existingRole.isEmpty()) {
                Role role = new Role();
                role.setRoleType(roleType);
                roleRepository.save(role);
                System.out.println("Created role: " + roleType);
            }
        });
    }

    private void initializeAdminUser(UtilisateurRepository utilisateurRepository,
                                     RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder) {
        if (utilisateurRepository.findByEmail("admin@gmail.com").isEmpty()) {
            Role adminRole = roleRepository.findFirstByRoleType(ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

            LocalDate birthDate = LocalDate.parse("2001-09-08");
            long age = calculateAge(birthDate);

            Utilisateur admin = new Utilisateur();
            admin.setNom("admin");
            admin.setPrenom("admin");
            admin.setEmail("admin@gmail.com");
            admin.setDateNaissance(birthDate);
            admin.setAge(age);
            admin.setTelephone("+237677850000");
            admin.setAdresse("Yaounde Mimboman Sapeur");
            admin.setGenre("Homme");
            admin.setPassword(passwordEncoder.encode("administrateur"));
            admin.setActif(true);
            admin.setRole(adminRole);

            utilisateurRepository.save(admin);
            System.out.println("Created default admin user with age: " + age);
        }
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private void initializeSalles(SalleRepository salleRepository) {
        Arrays.stream(ServiceMedical.values()).forEach(serviceMedical -> {
            if (salleRepository.findByServiceMedical(serviceMedical)==null) {
                Salle salle = new Salle();
                salle.setNumeroSalle("Salle-" + serviceMedical.name());
                salle.setServiceMedical(serviceMedical);
                salle.setStatutSalle(DISPONIBLE);
                salleRepository.save(salle);
                System.out.println("Created salle for service: " + serviceMedical);
            }
        });
    }
}