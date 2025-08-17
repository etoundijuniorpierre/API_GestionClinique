package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.ResponseDto.stats.*;
import com.example.GestionClinique.mapper.StatMapper;
import com.example.GestionClinique.service.StatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;


@Tag(name = "STATISTIQUES", description = "API pour la récupération des statistiques (jour, mois, année)")
@RestController
@RequestMapping(API_NAME + "/stats")
public class StatController {

    private final StatService statService;
    private final StatMapper statMapper;

    public StatController(StatService statService, StatMapper statMapper) {
        this.statService = statService;
        this.statMapper = statMapper;
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/daily")
    @Operation(summary = "Obtenir les statistiques journalières",
            description = "Récupère les statistiques pour une journée spécifique. Si la date n'est pas fournie, les statistiques du jour actuel sont retournées.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques journalières trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatDuJourResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Format de date invalide",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getDailyStats(
            @RequestParam(required = false) @Parameter(description = "Date au format YYYY-MM-DD (ex: 2025-07-19). Si omis, la date actuelle est utilisée.") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        StatDuJourResponseDto stats = statMapper.toStatDuJourDto(statService.getOrCreateStatDuJour(date));
        return ResponseEntity.ok(stats);
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/monthly")
    @Operation(summary = "Obtenir les statistiques mensuelles",
            description = "Récupère les statistiques agrégées pour un mois donné.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques du mois trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatParMoisResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Numéro de mois invalide",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getMonthlyStats(
            @RequestParam @Parameter(description = "Numéro du mois (1-12) ou mot-clé ('last', 'current').", example = "7") String month) {
        int monthNumber;
        switch (month.toLowerCase()) {
            case "last":
                monthNumber = LocalDate.now().minusMonths(1).getMonthValue();
                break;
            case "current":
                monthNumber = LocalDate.now().getMonthValue();
                break;
            default:
                try {
                    monthNumber = Integer.parseInt(month);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Le paramètre 'month' doit être un numéro (1-12) ou 'last'/'current'.");
                }
        }
        StatParMoisResponseDto stats = statMapper.toStatParMoisDto(statService.getOrCreateStatParMois(monthNumber));
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/yearly")
    @Operation(summary = "Obtenir les statistiques annuelles",
            description = "Récupère les statistiques agrégées pour une année spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques annuelles trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatsSurLanneeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Année invalide",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getYearlyStats(
            @RequestParam @Parameter(description = "Année (ex: 2025)", example = "2025") int year) {
        StatsSurLanneeResponseDto stats = statMapper.toStatsSurLanneeDto(statService.getOrCreateStatsSurLannee(year));
        return ResponseEntity.ok(stats);
    }
}