package com.example.GestionClinique.service.photoService;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.serviceImpl.UtilisateurServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;


@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;

    private final long maxFileSize;
    private final List<String> allowedExtensions;
    @Lazy
    private final UtilisateurServiceImpl utilisateurService;

    public FileStorageServiceImpl(
            @Value("${file.storage.directory}") String storageDir,
            @Value("${file.max-size}") long maxFileSize,
            @Value("${file.allowed-extensions}") String allowedExtensionsStr,
            @Lazy UtilisateurServiceImpl utilisateurService) {

        this.rootLocation = Paths.get(storageDir);
        this.maxFileSize = maxFileSize;
        this.allowedExtensions = Stream.of(allowedExtensionsStr.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();


        this.utilisateurService = utilisateurService;
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le dossier de stockage", e);
        }
    }

    public String save(MultipartFile file, Long userId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Le fichier est vide.");
            }

            if (file.getSize() > maxFileSize) {
                throw new RuntimeException("Le fichier dépasse la taille maximale autorisée.");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("Nom de fichier invalide.");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException("Extension de fichier non autorisée : " + extension);
            }

            Utilisateur existingUtilisateur = utilisateurService.findUtilisateurById(userId);

            String newFilename = "user_" + userId + "_" + existingUtilisateur.getNom()+"_"+existingUtilisateur.getPrenom()+ extension;
            Path destinationFile = rootLocation.resolve(newFilename).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            existingUtilisateur.setPhotoProfil(newFilename);
            utilisateurService.updateUtilisateur(existingUtilisateur.getId(), existingUtilisateur);

            return newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier.", e);
        }
    }

    public Resource load(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier : " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur d'URL du fichier : " + filename, e);
        }
    }

    public boolean delete(String filename) {
        if (filename == null || filename.isEmpty()) return false;

        Path filePath = rootLocation.resolve(filename).normalize();
        try {
            if (!filePath.startsWith(rootLocation)) {
                throw new IllegalArgumentException("Tentative de suppression d'un fichier hors du répertoire autorisé.");
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Échec de la suppression du fichier : " + filename + " - " + e.getMessage());
            return false;
        }
    }
}
