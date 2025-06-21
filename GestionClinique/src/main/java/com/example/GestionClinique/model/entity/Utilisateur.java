package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "utilisateurs")
public class Utilisateur extends EntityAbstracte {
    @Embedded
    private InfoPersonnel infoPersonnel;

    @Column(nullable = false, name = "mot_de_passe")
    @JsonIgnore // Ignoré pour la sérialisation JSON pour des raisons de sécurité
    @Size(min = 8, max = 20)
    private String motDePasse;

    @Column(name = "actif", nullable = false) // Ajouté nullable = false
    private Boolean actif;



    @ManyToMany(fetch = FetchType.EAGER) // Changé de OneToMany à ManyToMany
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> role = new ArrayList<>(); //
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertit chaque objet Role en SimpleGrantedAuthority
        // Le rôle doit être préfixé par "ROLE_" si vous utilisez hasRole("ADMIN") dans Spring Security
        // Sinon, si vous utilisez hasAuthority("ADMIN"), le préfixe n'est pas obligatoire
        return role.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType())) // Assurez-vous que Role a un getRoleType() qui retourne une String (ex: "ADMIN", "USER")
                .collect(Collectors.toSet());
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ServiceMedical") // Ajouté nullable = false
    private ServiceMedical serviceMedical;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messagesRecus = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté cascade et initialisation
    private List<HistoriqueAction> historiqueActions = new ArrayList<>();
}