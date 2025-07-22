package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.service.LoggingAspect;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient; // Need to import Patient entity
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.FactureRepository;
import com.example.GestionClinique.service.FactureService;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;



import com.itextpdf.layout.property.TextAlignment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final RendezVousRepository rendezVousRepository; // To fetch rendezVous
    private final ConsultationRepository consultationRepository;
    private final LoggingAspect loggingAspect;
    private final HistoriqueActionService historiqueActionService;


    public void generateInvoiceForRendesVous(Long rendezVousId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("rendezVous not found with ID: " + rendezVousId));

        if (factureRepository.findByRendezVousId(rendezVousId).isPresent()) {
            throw new RuntimeException("A facture already exists for Consultation with ID: " + rendezVousId);
        }

        Facture facture = new Facture();
        facture.setRendezVous(rendezVous);
        if (rendezVous.getPatient()  != null) {
            facture.setPatient(rendezVous.getPatient());
        } else {
            System.out.println("Warning: Creating invoice for rendezVous ID " + rendezVousId + " without an associated patient.");
            facture.setPatient(null);
        }

        facture.setDateEmission(LocalDateTime.now());
        facture.setStatutPaiement(StatutPaiement.IMPAYEE);
        facture.setModePaiement(ModePaiement.ESPECES);
        facture.setMontant(rendezVous.getMedecin().getServiceMedical().getMontant());

        Facture savedFacture = factureRepository.save(facture);

        rendezVous.setFacture(savedFacture);
        rendezVousRepository.save(rendezVous);

        historiqueActionService.enregistrerAction(
                String.format("Génération facture ID: %d pour rendez-vous ID: %d",
                        savedFacture.getId(), rendezVousId),
                loggingAspect.currentUserId()
        );

    }

    @Override
    public void generateInvoiceForConsultation(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

        if (factureRepository.findByConsultationId(consultationId).isPresent()) {
            throw new RuntimeException("A facture already exists for Consultation with ID: " + consultationId);
        }

        Facture facture = new Facture();
        facture.setConsultation(consultation);

        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
            facture.setPatient(consultation.getDossierMedical().getPatient());
        } else {
            System.out.println("Warning: Creating invoice for consultation ID " + consultationId + " without an associated patient.");
            facture.setPatient(null); // Explicitly set to null if no patient
        }

        facture.setDateEmission(LocalDateTime.now());
        facture.setStatutPaiement(StatutPaiement.IMPAYEE);
        facture.setModePaiement(ModePaiement.ESPECES);
        facture.setMontant(consultation.getMedecin().getServiceMedical().getMontant());

        Facture savedFacture = factureRepository.save(facture);

        consultation.setFacture(savedFacture);
        consultationRepository.save(consultation);

        historiqueActionService.enregistrerAction(
                String.format("Génération facture ID: %d pour rendez-vous ID: %d",
                        savedFacture.getId(), consultationId),
                loggingAspect.currentUserId()
        );
    }


    @Override
    public Facture updateFacture(Long id, Facture factureDetails) {
        Facture existingFacture = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));

        existingFacture.setMontant(factureDetails.getMontant());
        existingFacture.setDateEmission(factureDetails.getDateEmission());
        existingFacture.setStatutPaiement(factureDetails.getStatutPaiement());
        existingFacture.setModePaiement(factureDetails.getModePaiement());

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour facture ID: %d", id),
                loggingAspect.currentUserId()
        );

        return factureRepository.save(existingFacture);
    }

    @Override
    @Transactional
    public List<Facture> findAllFactures() {
        return factureRepository.findAll();
    }

    @Override
    @Transactional
    public List<Facture> findFacturesByStatut(StatutPaiement statutPaiement) {
        return factureRepository.findByStatutPaiement(statutPaiement);
    }

    @Override
    @Transactional
    public List<Facture> findFacturesByModePaiement(ModePaiement modePaiement) {
        return factureRepository.findByModePaiement(modePaiement);
    }

    @Override
    public List<Facture> findAllFacturesIMPAYE() {
        return findFacturesByStatut(StatutPaiement.IMPAYEE);
    }

    @Override
    @Transactional
    public Facture findById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteFacture(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));
        // Disassociate facture from rendezVous before deleting (bi-directional link)
        if (facture.getRendezVous() != null) {
            RendezVous rendezVous = facture.getRendezVous();
            rendezVous.setFacture(null);
            rendezVousRepository.save(rendezVous);
        }

        historiqueActionService.enregistrerAction(
                String.format("Suppression facture ID: %d", id),
                loggingAspect.currentUserId()
        );

        factureRepository.delete(facture);
    }

    @Override
    @Transactional
    public Patient findPatientByFactureId(Long id) {
        Facture facture = findById(id);
        // Ensure patient is not null before returning if the Facture entity allows null patients
        if (facture.getPatient() == null) {
            throw new IllegalStateException("Facture with ID: " + id + " does not have an associated patient.");
        }
        return facture.getPatient();
    }

//    @Override
//    public Facture updateStatutPaiement(Long factureId, StatutPaiement nouveauStatut) {
//        Facture facture = factureRepository.findById(factureId)
//                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + factureId));
//        facture.setStatutPaiement(nouveauStatut);
//        return factureRepository.save(facture);
//    }



    @Override
    public Facture payerFacture(Long factureId, ModePaiement modePaiement) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + factureId));

        if (facture.getStatutPaiement() == StatutPaiement.PAYEE) {
            throw new IllegalArgumentException("Facture with ID: " + factureId + " is already marked as PAID.");
        }
        facture.setModePaiement(modePaiement);
        facture.setStatutPaiement(StatutPaiement.PAYEE);
        facture.setDateEmission(LocalDateTime.now());
        historiqueActionService.enregistrerAction(
                String.format("Paiement facture ID: %d via %s",
                        factureId, modePaiement.toString()),
                loggingAspect.currentUserId()
        );

        return factureRepository.save(facture);
    }


    // FactureServiceImpl.java (within generateFacturePdf method)




    @Override
    public byte[] generateFacturePdf(Long factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + factureId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // --- FIXES HERE: Wrap strings in Paragraph or Text ---
            document.add(new Paragraph("FACTURE MÉDICALE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20));
            document.add(new Paragraph("--------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Facture ID: ").add(new Text(facture.getId().toString())));
            document.add(new Paragraph("Date d'émission: ").add(new Text(facture.getDateEmission().toString())));
            document.add(new Paragraph("Statut: ").add(new Text(facture.getStatutPaiement().toString())));
            document.add(new Paragraph("Mode de paiement: ").add(new Text(facture.getModePaiement().toString())));
            document.add(new Paragraph("Montant: ").add(new Text(String.format("%.2f", facture.getMontant()) + " XAF"))); // Assuming XAF as currency

            if (facture.getPatient() != null) {
                document.add(new Paragraph("Patient: ").add(new Text(facture.getPatient().getNom() + " " + facture.getPatient().getPrenom())));
            } else {
                document.add(new Paragraph("Patient: ").add(new Text("Non spécifié (Urgence)")));
            }

            if (facture.getRendezVous() != null) {
                document.add(new Paragraph("rendezVous ID: ").add(new Text(facture.getRendezVous().getId().toString())));
                if (facture.getRendezVous().getMedecin() != null) {
                    document.add(new Paragraph("Médecin: ").add(new Text(facture.getRendezVous().getMedecin().getNom() + " " + facture.getRendezVous().getMedecin().getPrenom())));
                }
                if (facture.getRendezVous().getConsultation().getMotifs() != null) {
                    document.add(new Paragraph("Motif rendezVous: ").add(new Text(facture.getRendezVous().getConsultation().getMotifs())));
                }
            }

            document.add(new Paragraph("\nMerci de votre confiance!").setTextAlignment(TextAlignment.CENTER));

        } catch (Exception e) { // Catch more specific exceptions if possible (e.g., IOException, DocumentException)
            System.err.println("Error generating PDF for Facture ID " + factureId + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate invoice PDF.", e);
        } finally {
            // Ensure document is closed even if an error occurs
            if (document != null) {
                document.close();
            }
        }

        historiqueActionService.enregistrerAction(
                String.format("gènèration PDF de la facture ID: %d", factureId),
                loggingAspect.currentUserId()
        );

        return baos.toByteArray();
    }
}

