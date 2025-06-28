package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Integer> {


    List<Message> findByExpediteur(Utilisateur expediteur);

    List<Message> findByDestinataire(Utilisateur destinataire);
}
