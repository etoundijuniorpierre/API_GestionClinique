package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.service.authService.SecurityUtil;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LoggingAspect {

    private final SecurityUtil securityUtil;

    public Long currentUserId () {
        return securityUtil.getCurrentAuthenticatedUserId();
    }
}
