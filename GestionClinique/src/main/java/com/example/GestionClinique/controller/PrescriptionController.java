package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PrescriptionResponseDto;
import com.example.GestionClinique.mapper.PrescriptionMapper;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Prescriptions", description = "API pour la gestion des prescriptions médicales")
@RequestMapping(API_NAME + "/prescriptions") // Plural and hyphenated for REST convention
@RestController
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, PrescriptionMapper prescriptionMapper) {
        this.prescriptionService = prescriptionService;
        this.prescriptionMapper = prescriptionMapper;
    }



@PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(path = "/{consultationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter une prescription à une consultation existante",
            description = "Enregistre une nouvelle prescription médicale et la lie à une consultation existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prescription créée avec succès",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de prescription invalides ou consultation, patient, médecin, ou dossier médical manquant dans la requête ou la consultation."),
            @ApiResponse(responseCode = "404", description = "Consultation non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<PrescriptionResponseDto> addPrescription(
            @Parameter(description = "ID de la consultation à laquelle la prescription sera ajoutée", required = true)
            @PathVariable("consultationId") Long consultationId, // Renamed 'id' to 'consultationId' for clarity
            @Parameter(description = "Détails de la prescription à créer", required = true)
            @Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto) {

        Prescription prescriptionToCreate = prescriptionMapper.toEntity(prescriptionRequestDto);

        Prescription createdPrescription = prescriptionService.addPrescription(consultationId, prescriptionToCreate);

        PrescriptionResponseDto responseDto = prescriptionMapper.toDto(createdPrescription);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }



@PreAuthorize("hasAnyRole('MEDECIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une prescription",
            description = "Modifie les informations d'une prescription existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class))), // ResponseDto
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Prescription, consultation, patient, médecin ou dossier médical introuvable"), // Clarified
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<PrescriptionResponseDto> updatePrescription(
            @Parameter(description = "ID de la prescription à mettre à jour", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Nouveaux détails de la prescription", required = true)
            @Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto) {

            Prescription existingPrescription = prescriptionService.findById(id);
            prescriptionMapper.updateEntityFromDto(prescriptionRequestDto, existingPrescription);
            Prescription updatedPrescription = prescriptionService.updatePrescription(id, existingPrescription);
            return ResponseEntity.ok(prescriptionMapper.toDto(updatedPrescription));

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')") // Patient can view their own, Secrétaire for administrative
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Obtenir une prescription par son ID",
            description = "Récupère les détails complets d'une prescription spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class))), // ResponseDto
            @ApiResponse(responseCode = "400", description = "ID de prescription invalide"),
            @ApiResponse(responseCode = "404", description = "Prescription introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<PrescriptionResponseDto> findById(
            @Parameter(description = "ID de la prescription à récupérer", required = true, example = "1")
            @PathVariable("id") Long id) {

            Prescription prescription = prescriptionService.findById(id);
            return ResponseEntity.ok(prescriptionMapper.toDto(prescription));

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE', 'ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path for all prescriptions
    @Operation(summary = "Lister toutes les prescriptions",
            description = "Récupère la liste complète des prescriptions enregistrées")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des prescriptions retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionResponseDto.class)))), // ArraySchema
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<PrescriptionResponseDto>> findAllPrescription() {
        List<Prescription> prescriptions = prescriptionService.findAllPrescription();
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptionMapper.toDtoList(prescriptions));
    }



@PreAuthorize("hasAnyRole('MEDECIN')")
    @DeleteMapping(path = "/{id}") // Simplified path
    @Operation(summary = "Supprimer une prescription",
            description = "Supprime définitivement une prescription du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prescription supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de prescription invalide"),
            @ApiResponse(responseCode = "404", description = "Prescription introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    public ResponseEntity<Void> deletePrescription(
            @Parameter(description = "ID de la prescription à supprimer", required = true, example = "1")
            @PathVariable("id") Long id) {

            prescriptionService.deletePrescription(id);
            return ResponseEntity.noContent().build(); // 204 No Content

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    @GetMapping(path = "/medecin/{medecinId}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Obtenir les prescriptions par médecin",
            description = "Récupère toutes les prescriptions rédigées par un médecin spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour ce médecin"),
            @ApiResponse(responseCode = "400", description = "ID de médecin invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<PrescriptionResponseDto>> findPrescriptionByMedecinId(
            @Parameter(description = "ID du médecin", required = true, example = "1")
            @PathVariable("medecinId") Long id) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionByMedecinId(id);
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptionMapper.toDtoList(prescriptions));
    }



@PreAuthorize("hasAnyRole('MEDECIN')") // Patient can view their own prescriptions
    @GetMapping(path = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Obtenir les prescriptions par patient",
            description = "Récupère toutes les prescriptions associées à un patient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour ce patient"),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<PrescriptionResponseDto>> findPrescriptionByPatientId(
            @Parameter(description = "ID du patient", required = true, example = "1")
            @PathVariable("patientId") Long id) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionByPatientId(id);
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptionMapper.toDtoList(prescriptions));
    }



@PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/consultation/{consultationId}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Obtenir les prescriptions par consultation",
            description = "Récupère toutes les prescriptions associées à une consultation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour cette consultation"),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<PrescriptionResponseDto>> findPrescriptionByConsultationId(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("consultationId") Long id) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionByConsultationId(id);
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptionMapper.toDtoList(prescriptions));
    }



}