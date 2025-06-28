package com.example.GestionClinique.controller;


import com.example.GestionClinique.configuration.security.jwtConfig.JwtResponse;
import com.example.GestionClinique.configuration.security.jwtConfig.JwtUtil;
import com.example.GestionClinique.controller.controllerApi.UtilisateurApi;
import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestRequestDto;
import com.example.GestionClinique.dto.dtoConnexion.LoginRequest;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class UtilisateurController implements UtilisateurApi {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }


    @Override
    public UtilisateurRequestRequestDto createUtilisateur(UtilisateurRequestRequestDto utilisateurRequestDto) {
        return utilisateurService.createUtilisateur(utilisateurRequestDto);
    }

    @Override
    public UtilisateurRequestRequestDto findUtilisateurById(Integer id) {
        return utilisateurService.findUtilisateurById(id);
    }

    @Override
    public List<UtilisateurRequestRequestDto> findUtilisateurByInfoPersonnel_Nom(String nom) {
        return utilisateurService.findUtilisateurByInfoPersonnel_Nom(nom);
    }

    @Override
    public UtilisateurRequestRequestDto findUtilisateurByInfoPersonnel_Email(String email) {
        return utilisateurService.findUtilisateurByInfoPersonnel_Email(email);
    }

    @Override
    public List<UtilisateurRequestRequestDto> findUtilisateurByRole_RoleType(RoleType roleType) {
        return utilisateurService.findUtilisateurByRole_RoleType(roleType);
    }

    @Override
    public List<UtilisateurRequestRequestDto> findAllUtilisateur() {
        return utilisateurService.findAllUtilisateur();
    }

    @Override
    public UtilisateurRequestRequestDto updateUtilisateur(Integer id, UtilisateurRequestRequestDto utilisateurRequestDto) {
        return utilisateurService.updateUtilisateur(id, utilisateurRequestDto);
    }

    @Override
    public UtilisateurRequestRequestDto updateUtilisateurStatus(Integer id, boolean isActive) {
        return utilisateurService.updateUtilisateurStatus(id, isActive);
    }

    @Override
    public void deleteUtilisateur(Integer id) {

        utilisateurService.deleteUtilisateur(id);
    }
}
