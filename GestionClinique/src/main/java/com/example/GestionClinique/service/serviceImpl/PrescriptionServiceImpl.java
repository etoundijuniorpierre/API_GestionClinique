package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.service.LoggingAspect;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.PrescriptionService;
import com.itextpdf.layout.property.TextAlignment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;

        @Override
        public Prescription addPrescription(Long consultationId, Prescription prescription) {

            Consultation consultation = consultationRepository.findById(consultationId)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

            prescription.setConsultation(consultation);

            if (consultation.getMedecin() == null ) {
                throw new IllegalStateException("Consultation, its Medecin, cannot add prescription.");
            }
            prescription.setMedecin(consultation.getMedecin());
            prescription.setDossierMedical(consultation.getDossierMedical());
            prescription.setPatient(consultation.getDossierMedical().getPatient());

            Prescription savedPrescription = prescriptionRepository.save(prescription);

            historiqueActionService.enregistrerAction(
                    String.format("Ajout prescription ID: %d pour consultation ID: %d",
                            savedPrescription.getId(), consultationId),
                    loggingAspect.currentUserId()
            );

            return savedPrescription;
    }

    @Override
    public Prescription updatePrescription(Long id, Prescription prescriptionDetails) {
        Prescription existingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));

        // Update scalar fields
        existingPrescription.setTypePrescription(prescriptionDetails.getTypePrescription());
        existingPrescription.setMedicaments(prescriptionDetails.getMedicaments());
        existingPrescription.setInstructions(prescriptionDetails.getInstructions());
        existingPrescription.setDureePrescription(prescriptionDetails.getDureePrescription());
        existingPrescription.setQuantite(prescriptionDetails.getQuantite());

        // Update associated entities if their IDs are provided and differ
        if (prescriptionDetails.getConsultation() != null && !prescriptionDetails.getConsultation().getId().equals(existingPrescription.getConsultation().getId())) {
            Consultation newConsultation = consultationRepository.findById(prescriptionDetails.getConsultation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + prescriptionDetails.getConsultation().getId()));
            existingPrescription.setConsultation(newConsultation);
        }
        if (prescriptionDetails.getMedecin() != null && !prescriptionDetails.getMedecin().getId().equals(existingPrescription.getMedecin().getId())) {
            Utilisateur newMedecin = utilisateurRepository.findById(prescriptionDetails.getMedecin().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + prescriptionDetails.getMedecin().getId()));
            existingPrescription.setMedecin(newMedecin);
        }
        if (prescriptionDetails.getPatient() != null && !prescriptionDetails.getPatient().getId().equals(existingPrescription.getPatient().getId())) {
            Patient newPatient = patientRepository.findById(prescriptionDetails.getPatient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + prescriptionDetails.getPatient().getId()));
            existingPrescription.setPatient(newPatient);
        }
        if (prescriptionDetails.getDossierMedical() != null && !prescriptionDetails.getDossierMedical().getId().equals(existingPrescription.getDossierMedical().getId())) {
            DossierMedical newDossierMedical = dossierMedicalRepository.findById(prescriptionDetails.getDossierMedical().getId())
                    .orElseThrow(() -> new IllegalArgumentException("DossierMedical not found with ID: " + prescriptionDetails.getDossierMedical().getId()));
            existingPrescription.setDossierMedical(newDossierMedical);
        }

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour prescription ID: %d", id),
                loggingAspect.currentUserId()
        );

        return prescriptionRepository.save(existingPrescription);
    }

    @Override
    @Transactional
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<Prescription> findAllPrescription() {
        return prescriptionRepository.findAll();
    }

    @Override
    public void deletePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));

        historiqueActionService.enregistrerAction(
                String.format("Suppression prescription ID: %d", id),
                loggingAspect.currentUserId()
        );

        prescriptionRepository.delete(prescription);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByMedecinId(Long medecinId) {
        return prescriptionRepository.findByMedecinId(medecinId);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByConsultationId(Long consultationId) {
        return prescriptionRepository.findByConsultationId(consultationId);
    }


    @Override
    public byte[] generatePrescriptionPdf(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // --- Header Section ---
            document.add(new Paragraph("ORDONNANCE MÉDICALE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(24));
            document.add(new Paragraph("--------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER));

            // --- Doctor Information (if available) ---
            if (prescription.getMedecin() != null) {
                document.add(new Paragraph("Médecin Prescripteur: ")
                        .add(new Text(prescription.getMedecin().getNom() + " " + prescription.getMedecin().getPrenom()))
                        .add("\nSpécialité: ")
                        .add(new Text(prescription.getMedecin().getServiceMedical() != null ? prescription.getMedecin().getServiceMedical().name() : "N/A"))
                        .add("\nDate: ")
                        .add(new Text(prescription.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                        .setTextAlignment(TextAlignment.RIGHT));
            } else {
                document.add(new Paragraph("Médecin: N/A"));
            }

            document.add(new Paragraph("\n"));

            // --- Patient Information (if available) ---
            if (prescription.getPatient() != null) {
                document.add(new Paragraph("Patient: ")
                        .add(new Text(prescription.getPatient().getNom() + " " + prescription.getPatient().getPrenom()))
                        .add("\nDate de naissance: ")
                        .add(new Text(prescription.getPatient().getDateNaissance() != null ? prescription.getPatient().getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"))
                        .add("\nSexe: ")
                        .add(new Text(prescription.getPatient().getGenre() != null ? prescription.getPatient().getGenre() : "N/A")));
            } else {
                document.add(new Paragraph("Patient: Non spécifié (Urgence)"));
            }

            document.add(new Paragraph("\n")); // Spacer

            // --- Prescription Details ---
            document.add(new Paragraph("Type de Prescription: ")
                    .add(new Text(prescription.getTypePrescription()).setBold()));
            document.add(new Paragraph("Médicaments/Articles: ")
                    .add(new Text(prescription.getMedicaments())));
            document.add(new Paragraph("Instructions: ")
                    .add(new Text(prescription.getInstructions())));
            document.add(new Paragraph("Quantité: ")
                    .add(new Text(prescription.getQuantite().toString())));
            if (prescription.getDureePrescription() != null && !prescription.getDureePrescription().isEmpty()) {
                document.add(new Paragraph("Durée: ")
                        .add(new Text(prescription.getDureePrescription())));
            }

            document.add(new Paragraph("\n")); // Spacer

            // --- Consultation Context (if available) ---
            if (prescription.getConsultation() != null) {
                document.add(new Paragraph("Consultation Associée:")
                        .setBold());
                document.add(new Paragraph("ID Consultation: ")
                        .add(new Text(prescription.getConsultation().getId().toString())));
                document.add(new Paragraph("Motif de Consultation: ")
                        .add(new Text(prescription.getConsultation().getMotifs())));
                document.add(new Paragraph("Diagnostic: ")
                        .add(new Text(prescription.getConsultation().getDiagnostic())));
            }


            // --- Footer ---
            document.add(new Paragraph("\n\n--------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Signature du médecin")
                    .setTextAlignment(TextAlignment.RIGHT));


        } catch (Exception e) {
            System.err.println("Error generating PDF for Prescription ID " + prescriptionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate prescription PDF.", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }

        historiqueActionService.enregistrerAction(
                String.format("gènèration PDF de la facture ID: %d", prescriptionId),
                loggingAspect.currentUserId()
        );

        return baos.toByteArray();
    }
}