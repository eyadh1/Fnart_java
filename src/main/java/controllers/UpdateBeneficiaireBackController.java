package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

public class UpdateBeneficiaireBackController {
    @FXML
    private TextField nomTextField;
    
    @FXML
    private TextField emailTextField;
    
    @FXML
    private TextField telephoneTextField;
    
    @FXML
    private TextField causeTextField;
    
    @FXML
    private TextField associationTextField;
    
    @FXML
    private TextField valeurTextField;
    
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button closeButton;
    
    private beneficiaires currentBeneficiaire;
    private ServicesBeneficiaires servicesBeneficiaires;
    
    @FXML
    public void initialize() {
        servicesBeneficiaires = new ServicesBeneficiaires();
        statusChoiceBox.getItems().addAll("En attente", "Accepté", "Refusé");
    }
    
    public void setBeneficiaire(beneficiaires beneficiaire) {
        this.currentBeneficiaire = beneficiaire;
        displayBeneficiaireDetails();
    }
    
    private void displayBeneficiaireDetails() {
        if (currentBeneficiaire != null) {
            nomTextField.setText(currentBeneficiaire.getNom());
            emailTextField.setText(currentBeneficiaire.getEmail());
            telephoneTextField.setText(currentBeneficiaire.getTelephone());
            causeTextField.setText(currentBeneficiaire.getCause());
            associationTextField.setText(currentBeneficiaire.getEstElleAssociation());
            valeurTextField.setText(String.valueOf(currentBeneficiaire.getValeurDemande()));
            statusChoiceBox.setValue(currentBeneficiaire.getStatus());
            descriptionArea.setText(currentBeneficiaire.getDescription());
        }
    }
    
    @FXML
    private void handleUpdate() {
        try {
            // Validate input
            if (nomTextField.getText().isEmpty() || emailTextField.getText().isEmpty() || 
                telephoneTextField.getText().isEmpty() || causeTextField.getText().isEmpty() || 
                associationTextField.getText().isEmpty() || valeurTextField.getText().isEmpty() || 
                statusChoiceBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                return;
            }
            
            // Update beneficiaire object
            currentBeneficiaire.setNom(nomTextField.getText());
            currentBeneficiaire.setEmail(emailTextField.getText());
            currentBeneficiaire.setTelephone(telephoneTextField.getText());
            currentBeneficiaire.setCause(causeTextField.getText());
            currentBeneficiaire.setEstElleAssociation(associationTextField.getText());
            currentBeneficiaire.setValeurDemande(Double.parseDouble(valeurTextField.getText()));
            currentBeneficiaire.setStatus(statusChoiceBox.getValue());
            currentBeneficiaire.setDescription(descriptionArea.getText());
            
            // Save to database
            servicesBeneficiaires.update(currentBeneficiaire);
            
            // Show success message
            showSuccessAlert("Succès", "Le bénéficiaire a été mis à jour avec succès");
            
            // Close the window
            handleClose();
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La valeur demandée doit être un nombre valide");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 