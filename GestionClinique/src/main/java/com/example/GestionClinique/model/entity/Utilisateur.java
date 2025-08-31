package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.InfoPersonnel;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "utilisateurs")
public class Utilisateur extends InfoPersonnel {

    @Column(nullable = false, name = "mot_de_passe")
    @JsonIgnore
    @Size(min = 8, max = 20)
    private String password;

    private Boolean actif;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "photo_profil")
    private String photoProfil;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.getRoleType().name()));
    }

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_logout_date")
    private LocalDateTime lastLogoutDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ServiceMedical")
    private ServiceMedical serviceMedical = ServiceMedical.MEDECINE_GENERALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_connect", nullable = false)
    private StatusConnect statusConnect = StatusConnect.DECONNECTE;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoriqueAction> historiqueActions = new ArrayList<>();

}