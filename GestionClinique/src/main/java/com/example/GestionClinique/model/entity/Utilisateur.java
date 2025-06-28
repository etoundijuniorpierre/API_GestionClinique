package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.InfoPersonnel;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "utilisateurs")
public class Utilisateur extends InfoPersonnel{

    @Column(nullable = false, name = "mot_de_passe")
    @JsonIgnore
    @Size(min = 8, max = 20)
    private String motDePasse;

    @Column(nullable = false, unique = true)
    private String email;

    private Boolean actif;

    @ManyToMany(fetch = FetchType.EAGER) // Keep EAGER or consider LAZY + specific fetching later
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> role = new HashSet<>(); // Changed from List to Set, and ArrayList to HashSet

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name())) // Ensure .name() for enum
                .collect(Collectors.toSet());
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ServiceMedical")
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

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoriqueAction> historiqueActions = new ArrayList<>();
}