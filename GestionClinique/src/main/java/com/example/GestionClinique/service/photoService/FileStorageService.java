package com.example.GestionClinique.service.photoService;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String save(MultipartFile file, Long userId);
    Resource load(String filename);
    boolean delete(String filename);
    void init();
}
