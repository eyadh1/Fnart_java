package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.models.commande;
import tn.esprit.service.servicecommande;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ajoutercommandeController {

    @FXML
    private TextField artwork_idTextField;

    @FXML
    private TextField nomTextField;

    @FXML
    private TextField adressTextField;

    @FXML
    private TextField telephoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private Button btnajout;

    private artwork selectedArtwork;

    public void initData(artwork artwork) {
        this.selectedArtwork = artwork;
        artwork_idTextField.setText(String.valueOf(artwork.getId()));
        artwork_idTextField.setEditable(false); // L'ID de l'artwork ne doit pas être modifiable
    }

    @FXML
    void ajouterCommande(ActionEvent event) {
        if (validateFields()) {
            try {
                commande newCommande = new commande(
                    nomTextField.getText(),
                    adressTextField.getText(),
                    telephoneTextField.getText(),
                    emailTextField.getText(),
                    selectedArtwork.getId(),
                    Date.from(dateDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    selectedArtwork.getPrix(),
                    "En attente"
                );

                servicecommande service = new servicecommande();
                service.add(newCommande);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "La commande a été ajoutée avec succès!");
                closeWindow();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la commande: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        if (nomTextField.getText().isEmpty() || 
            adressTextField.getText().isEmpty() ||
            telephoneTextField.getText().isEmpty() || 
            emailTextField.getText().isEmpty() || 
            dateDatePicker.getValue() == null) {
            
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez remplir tous les champs");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnajout.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        // Initialiser la date à aujourd'hui
        dateDatePicker.setValue(LocalDate.now());
    }
}
