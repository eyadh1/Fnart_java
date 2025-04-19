package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.Beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddBeneficiaireBackController implements Initializable {

    @FXML
    private TextField NomTextField;
    @FXML
    private TextField EmailTextField;
    @FXML
    private TextField TelephoneTextField;
    @FXML
    private ChoiceBox<String> AssociationChoice;
    @FXML
    private TextField CauseTextField;
    @FXML
    private TextField ValeurTextField;
    @FXML
    private TextArea DescriptionTextArea;
    @FXML
    private ChoiceBox<String> StatusChoice;
    @FXML
    private Button AjoutButton;
    @FXML
    private Button backButton;

    private ServicesBeneficiaires servicesBeneficiaires;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicesBeneficiaires = new ServicesBeneficiaires();


        // Initialize status choices
        StatusChoice.getItems().addAll("En attente", "Accepté", "Rejeté");
        StatusChoice.setValue("En attente");

        // Initialize association choices
        AssociationChoice.getItems().addAll("Oui", "Non");
        AssociationChoice.setValue("Non");

        // Set up button actions
        AjoutButton.setOnAction(event -> handleSubmit());
        backButton.setOnAction(event -> handleBack());
    }

    @FXML
    private void handleSubmit() {
        try {
            // Validate input
            if (NomTextField.getText().isEmpty() || EmailTextField.getText().isEmpty() || 
                TelephoneTextField.getText().isEmpty() || AssociationChoice.getValue() == null ||
                CauseTextField.getText().isEmpty() || ValeurTextField.getText().isEmpty() ||
                DescriptionTextArea.getText().isEmpty()) {
                showError("Erreur de validation", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Create new beneficiaire
            Beneficiaires newBeneficiaire = new Beneficiaires();
            newBeneficiaire.setNom(NomTextField.getText());
            newBeneficiaire.setEmail(EmailTextField.getText());
            newBeneficiaire.setTelephone(TelephoneTextField.getText());
            newBeneficiaire.setEstElleAssociation(AssociationChoice.getValue());
            newBeneficiaire.setCause(CauseTextField.getText());
            newBeneficiaire.setValeurDemande(Double.parseDouble(ValeurTextField.getText()));
            newBeneficiaire.setDescription(DescriptionTextArea.getText());
            newBeneficiaire.setStatus(StatusChoice.getValue());

            // Add to database
            servicesBeneficiaires.add(newBeneficiaire);

            // Show success message
            showSuccess("Succès", "Le bénéficiaire a été ajouté avec succès.");

            // Navigate back to list
            handleBack();

        } catch (NumberFormatException e) {
            showError("Erreur de format", "La valeur demandée doit être un nombre valide.");
        } catch (Exception e) {
            showError("Erreur", "Une erreur est survenue lors de l'ajout du bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeBeneficiairesBack.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Bénéficiaires");
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de retourner à la liste des bénéficiaires: " + e.getMessage());
        }
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 