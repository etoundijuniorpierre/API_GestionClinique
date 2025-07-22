package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.ResponseDto.stats.StatDuJourResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatMoisDernierResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatMoisEncoursResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatsSurLanneeResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) @Parameter(description = "Date au format YYYY-MM-DD (ex: 2025-07-19). Si omis, la date actuelle est utilisée.") String date) {
            StatDuJourResponseDto stats = statMapper.toStatDuJourDto(statService.getOrCreateStatDuJour(LocalDate.parse(date)));
            return ResponseEntity.ok(stats);
    }


    @GetMapping("/last-month")
    @Operation(summary = "Obtenir les statistiques du mois dernier",
            description = "Récupère les statistiques agrégées pour le mois précédent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques du mois dernier trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatMoisDernierResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getLastMonthStats() {
            StatMoisDernierResponseDto stats = statMapper.toStatMoisDernierDto(statService.getOrCreateStatMoisDernier());
            return ResponseEntity.ok(stats);
    }


    @GetMapping("/current-month")
    @Operation(summary = "Obtenir les statistiques du mois en cours",
            description = "Récupère les statistiques agrégées pour le mois actuel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques du mois en cours trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatMoisEncoursResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getCurrentMonthStats() {
            StatMoisEncoursResponseDto stats = statMapper.toStatMoisEncoursDto(statService.getOrCreateStatMoisEncours());
            return ResponseEntity.ok(stats);
    }



    @GetMapping("/yearly/{year}")
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
            @PathVariable @Parameter(description = "Année (ex: 2025)") int year) {
            StatsSurLanneeResponseDto stats = statMapper.toStatsSurLanneeDto(statService.getOrCreateStatsSurLannee(year));
            return ResponseEntity.ok(stats);
    }
}