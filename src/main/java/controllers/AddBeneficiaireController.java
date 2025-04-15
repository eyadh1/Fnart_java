package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.IOException;

public class AddBeneficiaireController implements Initializable {

    @FXML
    private ChoiceBox<String> AssociationChoice;
    @FXML
    private Button AjoutBoutton;


    @FXML
    private TextField CauseTextField;

    @FXML
    private TextArea DescriptionTextArea;

    @FXML
    private TextField EmailTextField;

    @FXML
    private Button ListeBenebutton;

    @FXML
    private TextField NomTextField;

    @FXML
    private TextField TelephoneTextField;

    @FXML
    private TextField ValeurTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AssociationChoice.getItems().addAll("Oui", "Non");
        AssociationChoice.setValue("Non");

        AjoutBoutton.setOnAction(event -> handleSubmit());
        ListeBenebutton.setOnAction(event -> handleListeBene());
    }

    private void handleSubmit() {

        if (NomTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champ manquant");
            alert.setContentText("Le champ 'Nom' est obligatoire.");
            alert.showAndWait();
            return;
        }
        beneficiaires b = new beneficiaires();
        b.setNom(NomTextField.getText());
        b.setEmail(EmailTextField.getText());
        b.setTelephone(TelephoneTextField.getText());
        b.setEstElleAssociation(AssociationChoice.getValue());
        b.setCause(CauseTextField.getText());
        try {
            if (!ValeurTextField.getText().isEmpty()) {
                b.setValeurDemande(Double.parseDouble(ValeurTextField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La valeur demandée doit être un nombre valide");
            return;
        }
        b.setDescription(DescriptionTextArea.getText());

        ServicesBeneficiaires s = new ServicesBeneficiaires();
        s.add(b);
        
        // Show success alert
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Votre demande a été soumise avec succès !");
        successAlert.showAndWait();
        
        // Clear all fields
        NomTextField.clear();
        EmailTextField.clear();
        TelephoneTextField.clear();
        AssociationChoice.setValue("Non");
        CauseTextField.clear();
        ValeurTextField.clear();
        DescriptionTextArea.clear();
        
        System.out.println("Beneficiaire ajouté avec succès !");
    }

    private void handleListeBene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeBeneficiaires.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ListeBenebutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

