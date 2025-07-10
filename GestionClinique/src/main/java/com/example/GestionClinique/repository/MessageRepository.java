package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByExpediteurId(Long expediteurId); // Changed to Long
    List<Message> findByDestinataireId(Long destinataireId);
}
