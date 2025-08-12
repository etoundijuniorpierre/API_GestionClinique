package com.example.GestionClinique.controller;

import com.example.GestionClinique.configuration.utils.Constants;
import com.example.GestionClinique.dto.messagerieDto.GroupeRequestDto;
import com.example.GestionClinique.dto.messagerieDto.GroupeResponseDto;
import com.example.GestionClinique.mapper.GroupeMapper;
import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.service.serviceImpl.GroupeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    @RequestMapping(Constants.API_NAME + "/messagerie/groupes")
    @RequiredArgsConstructor
    public class GroupeController {

        private final GroupeService groupeService;
        private final GroupeMapper groupeMapper;

        @PostMapping
        public ResponseEntity<GroupeResponseDto> creerGroupe(@RequestBody GroupeRequestDto dto) {
            Groupe groupe = groupeService.creerGroupe(
                    dto.getNom(),
                    dto.getDescription(),
                    dto.getIdCreateur(),
                    dto.getIdsMembres()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(groupeMapper.toDto(groupe));
        }
    }



