package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.Artwork;
import tn.esprit.services.serviceartwork;
import tn.esprit.interfaces.ParentControllerAware;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class modifierartworkController implements ParentControllerAware {

    private Artwork currentArtwork;

    @FXML
    private TextField artistenomTextField;

    @FXML
    private Button btnsave;

    @FXML
    private TextField descriprtionTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField titreTextField;

    private listartwork parentController;

    @FXML
    void initialize() {
        setupValidationListeners();
    }

    private void setupValidationListeners() {
        // Validation du prix (nombres uniquement)
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        prixTextField.setTextFormatter(new TextFormatter<>(numericFilter));

        // Validation du titre
        titreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                titreTextField.setStyle("-fx-border-color: red;");
            } else {
                titreTextField.setStyle("");
            }
        });

        // Validation de la description
        descriprtionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                descriprtionTextField.setStyle("-fx-border-color: red;");
            } else {
                descriprtionTextField.setStyle("");
            }
        });

        // Validation du nom de l'artiste
        artistenomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                artistenomTextField.setStyle("-fx-border-color: red;");
            } else {
                artistenomTextField.setStyle("");
            }
        });

        // Validation du statut
        statusTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().toLowerCase().matches("(disponible|vendu|en_exposition)")) {
                statusTextField.setStyle("-fx-border-color: red;");
            } else {
                statusTextField.setStyle("");
            }
        });
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        // Validation du titre
        if (titreTextField.getText().trim().isEmpty()) {
            errorMessage.append("Le titre est obligatoire.\n");
        }

        // Validation de la description
        if (descriprtionTextField.getText().trim().isEmpty()) {
            errorMessage.append("La description est obligatoire.\n");
        }

        // Validation du prix
        try {
            int prix = Integer.parseInt(prixTextField.getText().trim());
            if (prix <= 0) {
                errorMessage.append("Le prix doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Le prix doit être un nombre valide.\n");
        }

        // Validation du nom de l'artiste
        if (artistenomTextField.getText().trim().isEmpty()) {
            errorMessage.append("Le nom de l'artiste est obligatoire.\n");
        }

        // Validation de l'image
        if (imageTextField.getText().isEmpty()) {
            errorMessage.append("Le chemin de l'image est obligatoire.\n");
        } else if (!imageTextField.getText().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            errorMessage.append("Le format de l'image doit être .jpg, .jpeg, .png ou .gif.\n");
        }

        // Validation du statut
        if (statusTextField.getText().isEmpty()) {
            errorMessage.append("Le statut est obligatoire.\n");
        } else if (!statusTextField.getText().toLowerCase().matches("(disponible|vendu|en_exposition)")) {
            errorMessage.append("Le statut doit être 'disponible', 'vendu' ou 'en_exposition'.\n");
        }

        // Afficher les erreurs s'il y en a
        if (errorMessage.length() > 0) {
            showAlert("Erreur de validation", errorMessage.toString());
            return false;
        }

        return true;
    }

    public void setArtwork(Artwork a) {
        if (a == null) {
            System.out.println("Erreur : artwork est null");
            return;
        }

        this.currentArtwork = a;
        populateFields();
    }

    private void populateFields() {
        if (currentArtwork != null) {
            titreTextField.setText(currentArtwork.getTitre() != null ? currentArtwork.getTitre() : "");
            descriprtionTextField.setText(currentArtwork.getDescription() != null ? currentArtwork.getDescription() : "");
            prixTextField.setText(String.valueOf(currentArtwork.getPrix()));
            imageTextField.setText(currentArtwork.getImage() != null ? currentArtwork.getImage() : "");
            artistenomTextField.setText(currentArtwork.getArtistenom() != null ? currentArtwork.getArtistenom() : "");
            statusTextField.setText(currentArtwork.getStatus() != null ? currentArtwork.getStatus() : "");
        }
    }

    @FXML
    private void updateart(ActionEvent event) {
        if (validateFields()) {
            try {
                currentArtwork.setTitre(titreTextField.getText().trim());
                currentArtwork.setDescription(descriprtionTextField.getText().trim());
                currentArtwork.setPrix(Integer.parseInt(prixTextField.getText()));
                currentArtwork.setImage(imageTextField.getText().trim());
                currentArtwork.setArtistenom(artistenomTextField.getText().trim());
                currentArtwork.setStatus(statusTextField.getText().toLowerCase());

                new serviceartwork().update(currentArtwork);

                showAlert("Succès", "Œuvre modifiée avec succès !");

                if (parentController != null) {
                    parentController.refreshTable();
                }

                Stage stage = (Stage) titreTextField.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue lors de la modification : " + e.getMessage());
            }
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setParentController(Object controller) {
        if (controller instanceof listartwork) {
            this.parentController = (listartwork) controller;
        } else {
            throw new IllegalArgumentException("Parent controller must be of type listartwork");
        }
    }
}