package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.Beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateBeneficiaireController implements Initializable {

    @FXML
    private ChoiceBox<String> AssociationChoice;
    @FXML
    private Button UpdateButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button BackButton;
    @FXML
    private TextField CauseTextField;
    @FXML
    private TextArea DescriptionTextArea;
    @FXML
    private TextField EmailTextField;
    @FXML
    private TextField NomTextField;
    @FXML
    private TextField TelephoneTextField;
    @FXML
    private TextField ValeurTextField;

    private Beneficiaires selectedBeneficiaire;
    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();

    public void setBeneficiaire(Beneficiaires beneficiaire) {
        this.selectedBeneficiaire = beneficiaire;
        if (beneficiaire != null) {
            NomTextField.setText(beneficiaire.getNom());
            EmailTextField.setText(beneficiaire.getEmail());
            TelephoneTextField.setText(beneficiaire.getTelephone());
            AssociationChoice.setValue(beneficiaire.getEstElleAssociation());
            CauseTextField.setText(beneficiaire.getCause());
            ValeurTextField.setText(beneficiaire.getValeurDemande() != null ? beneficiaire.getValeurDemande().toString() : "");
            DescriptionTextArea.setText(beneficiaire.getDescription());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AssociationChoice.getItems().addAll("Oui", "Non");
        AssociationChoice.setValue("Non");

        UpdateButton.setOnAction(event -> handleUpdate());
        DeleteButton.setOnAction(event -> handleDelete());
        BackButton.setOnAction(event -> handleBack());
    }

    private void handleUpdate() {
        if (selectedBeneficiaire == null) {
            showAlert("Erreur", "Aucun bénéficiaire sélectionné");
            return;
        }

        if (NomTextField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Nom' est obligatoire");
            return;
        }

        selectedBeneficiaire.setNom(NomTextField.getText());
        selectedBeneficiaire.setEmail(EmailTextField.getText());
        selectedBeneficiaire.setTelephone(TelephoneTextField.getText());
        selectedBeneficiaire.setEstElleAssociation(AssociationChoice.getValue());
        selectedBeneficiaire.setCause(CauseTextField.getText());
        try {
            if (!ValeurTextField.getText().isEmpty()) {
                selectedBeneficiaire.setValeurDemande(Double.parseDouble(ValeurTextField.getText()));
            } else {
                selectedBeneficiaire.setValeurDemande(null);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La valeur demandée doit être un nombre valide");
            return;
        }
        selectedBeneficiaire.setDescription(DescriptionTextArea.getText());

        servicesBeneficiaires.update(selectedBeneficiaire);
        
        showAlert("Succès", "Bénéficiaire modifié avec succès");
        handleBack();
    }

    private void handleDelete() {
        if (selectedBeneficiaire == null) {
            showAlert("Erreur", "Aucun bénéficiaire sélectionné");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le bénéficiaire");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce bénéficiaire ?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            servicesBeneficiaires.delete(selectedBeneficiaire);
            showAlert("Succès", "Bénéficiaire supprimé avec succès");
            handleBack();
        }
    }

    private void handleBack() {
        try {
            Stage stage = (Stage) BackButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 