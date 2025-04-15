package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

public class modifierartworkController {

    private artwork currentArtwork;

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
        // Validation en temps réel du prix (nombres uniquement)
        prixTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                prixTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Validation en temps réel du titre (max 100 caractères)
        titreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                titreTextField.setText(oldValue);
            }
        });

        // Validation en temps réel de la description (max 500 caractères)
        descriprtionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                descriprtionTextField.setText(oldValue);
            }
        });

        // Validation en temps réel du nom d'artiste (max 100 caractères)
        artistenomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                artistenomTextField.setText(oldValue);
            }
        });
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        // Validation du titre
        if (titreTextField.getText().isEmpty()) {
            errorMessage.append("Le titre est obligatoire.\n");
        } else if (titreTextField.getText().length() < 3 || titreTextField.getText().length() > 100) {
            errorMessage.append("Le titre doit contenir entre 3 et 100 caractères.\n");
        }

        // Validation de la description
        if (descriprtionTextField.getText().isEmpty()) {
            errorMessage.append("La description est obligatoire.\n");
        } else if (descriprtionTextField.getText().length() < 10 || descriprtionTextField.getText().length() > 500) {
            errorMessage.append("La description doit contenir entre 10 et 500 caractères.\n");
        }

        // Validation du prix
        if (prixTextField.getText().isEmpty()) {
            errorMessage.append("Le prix est obligatoire.\n");
        } else {
            try {
                int prix = Integer.parseInt(prixTextField.getText());
                if (prix <= 0) {
                    errorMessage.append("Le prix doit être supérieur à 0.\n");
                }
                if (prix > 1000000) {
                    errorMessage.append("Le prix ne peut pas dépasser 1,000,000.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le prix doit être un nombre entier valide.\n");
            }
        }

        // Validation du nom d'artiste
        if (artistenomTextField.getText().isEmpty()) {
            errorMessage.append("Le nom de l'artiste est obligatoire.\n");
        } else if (artistenomTextField.getText().length() < 3 || artistenomTextField.getText().length() > 100) {
            errorMessage.append("Le nom de l'artiste doit contenir entre 3 et 100 caractères.\n");
        }

        // Validation de l'image (vérification basique du format)
        if (imageTextField.getText().isEmpty()) {
            errorMessage.append("Le chemin de l'image est obligatoire.\n");
        } else if (!imageTextField.getText().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            errorMessage.append("Le format de l'image doit être .jpg, .jpeg, .png ou .gif.\n");
        }

        // Validation du statut
        if (statusTextField.getText().isEmpty()) {
            errorMessage.append("Le statut est obligatoire.\n");
        } else if (!statusTextField.getText().matches("(?i)(disponible|vendu|en_exposition)")) {
            errorMessage.append("Le statut doit être 'disponible', 'vendu' ou 'en_exposition'.\n");
        }

        // Afficher les erreurs s'il y en a
        if (errorMessage.length() > 0) {
            showAlert("Erreur de validation", errorMessage.toString());
            return false;
        }

        return true;
    }

    public void setArtwork(artwork a) {
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

    public void setParentController(listartwork controller) {
        this.parentController = controller;
    }
}
