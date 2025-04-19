package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.models.Beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

public class DetailBeneficiaireBackController {
    @FXML
    private Label nomLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Label telephoneLabel;
    
    @FXML
    private Label causeLabel;
    
    @FXML
    private Label associationLabel;
    
    @FXML
    private Label valeurLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button closeButton;
    
    private Beneficiaires currentBeneficiaire;
    private ServicesBeneficiaires servicesBeneficiaire;
    
    @FXML
    public void initialize() {
        servicesBeneficiaire = new ServicesBeneficiaires();
    }
    
    public void setBeneficiaire(Beneficiaires beneficiaire) {
        this.currentBeneficiaire = beneficiaire;
        displayBeneficiaireDetails();
    }
    
    private void displayBeneficiaireDetails() {
        if (currentBeneficiaire != null) {
            nomLabel.setText(currentBeneficiaire.getNom());
            emailLabel.setText(currentBeneficiaire.getEmail());
            telephoneLabel.setText(currentBeneficiaire.getTelephone());
            causeLabel.setText(currentBeneficiaire.getCause());
            associationLabel.setText(currentBeneficiaire.getEstElleAssociation());
            valeurLabel.setText(String.valueOf(currentBeneficiaire.getValeurDemande()));
            statusLabel.setText(currentBeneficiaire.getStatus());
            descriptionArea.setText(currentBeneficiaire.getDescription());
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
} 