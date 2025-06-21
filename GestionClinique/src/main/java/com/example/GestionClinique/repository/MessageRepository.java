package com.example.GestionClinique.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import io.micrometer.observation.ObservationFilter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Integer> {


    List<Message> findByExpediteur(Utilisateur expediteur);

    List<Message> findByDestinataire(Utilisateur destinataire);
}
