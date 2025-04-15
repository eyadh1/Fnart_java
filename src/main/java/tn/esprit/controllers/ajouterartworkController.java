package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ajouterartworkController {

    private static final String VALID_STYLE = "-fx-border-color: #00ff00;";
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";
    private static final String DEFAULT_STYLE = "";

    @FXML
    private Text ArtworkText;

    @FXML
    private TextField artistenomTextField;

    @FXML
    private Button btnSave;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField titreTextField;

    @FXML
    void initialize() {
        setupValidationListeners();
        // Ajouter les listeners pour la validation en temps réel
        titreTextField.textProperty().addListener((observable, oldValue, newValue) -> validateTextField(titreTextField, newValue, 3, 100));
        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> validateTextField(descriptionTextField, newValue, 10, 500));
        artistenomTextField.textProperty().addListener((observable, oldValue, newValue) -> validateTextField(artistenomTextField, newValue, 3, 100));
        prixTextField.textProperty().addListener((observable, oldValue, newValue) -> validatePrixField(newValue));
        imageTextField.textProperty().addListener((observable, oldValue, newValue) -> validateImageField(newValue));
        statusTextField.textProperty().addListener((observable, oldValue, newValue) -> validateStatusField(newValue));
    }

    private void validateTextField(TextField textField, String text, int minLength, int maxLength) {
        if (text.isEmpty()) {
            textField.setStyle(INVALID_STYLE);
        } else if (text.length() < minLength || text.length() > maxLength) {
            textField.setStyle(INVALID_STYLE);
        } else {
            textField.setStyle(VALID_STYLE);
        }
    }

    private void validatePrixField(String text) {
        if (text.isEmpty()) {
            prixTextField.setStyle(INVALID_STYLE);
            return;
        }
        try {
            int prix = Integer.parseInt(text);
            if (prix <= 0 || prix > 1000000) {
                prixTextField.setStyle(INVALID_STYLE);
            } else {
                prixTextField.setStyle(VALID_STYLE);
            }
        } catch (NumberFormatException e) {
            prixTextField.setStyle(INVALID_STYLE);
        }
    }

    private void validateImageField(String text) {
        if (text.isEmpty()) {
            imageTextField.setStyle(INVALID_STYLE);
        } else if (text.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            imageTextField.setStyle(VALID_STYLE);
        } else {
            imageTextField.setStyle(INVALID_STYLE);
        }
    }

    private void validateStatusField(String text) {
        if (text.isEmpty()) {
            statusTextField.setStyle(INVALID_STYLE);
        } else if (text.toLowerCase().matches("(disponible|vendu|en_exposition)")) {
            statusTextField.setStyle(VALID_STYLE);
        } else {
            statusTextField.setStyle(INVALID_STYLE);
        }
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
        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                descriptionTextField.setText(oldValue);
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
        boolean isValid = true;

        // Validation du titre
        if (titreTextField.getText().isEmpty()) {
            errorMessage.append("Le titre est obligatoire.\n");
            titreTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else if (titreTextField.getText().length() < 3 || titreTextField.getText().length() > 100) {
            errorMessage.append("Le titre doit contenir entre 3 et 100 caractères.\n");
            titreTextField.setStyle(INVALID_STYLE);
            isValid = false;
        }

        // Validation de la description
        if (descriptionTextField.getText().isEmpty()) {
            errorMessage.append("La description est obligatoire.\n");
            descriptionTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else if (descriptionTextField.getText().length() < 10 || descriptionTextField.getText().length() > 500) {
            errorMessage.append("La description doit contenir entre 10 et 500 caractères.\n");
            descriptionTextField.setStyle(INVALID_STYLE);
            isValid = false;
        }

        // Validation du prix
        if (prixTextField.getText().isEmpty()) {
            errorMessage.append("Le prix est obligatoire.\n");
            prixTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else {
            try {
                int prix = Integer.parseInt(prixTextField.getText());
                if (prix <= 0) {
                    errorMessage.append("Le prix doit être supérieur à 0.\n");
                    prixTextField.setStyle(INVALID_STYLE);
                    isValid = false;
                }
                if (prix > 1000000) {
                    errorMessage.append("Le prix ne peut pas dépasser 1,000,000.\n");
                    prixTextField.setStyle(INVALID_STYLE);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le prix doit être un nombre entier valide.\n");
                prixTextField.setStyle(INVALID_STYLE);
                isValid = false;
            }
        }

        // Validation du nom d'artiste
        if (artistenomTextField.getText().isEmpty()) {
            errorMessage.append("Le nom de l'artiste est obligatoire.\n");
            artistenomTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else if (artistenomTextField.getText().length() < 3 || artistenomTextField.getText().length() > 100) {
            errorMessage.append("Le nom de l'artiste doit contenir entre 3 et 100 caractères.\n");
            artistenomTextField.setStyle(INVALID_STYLE);
            isValid = false;
        }

        // Validation de l'image
        if (imageTextField.getText().isEmpty()) {
            errorMessage.append("Le chemin de l'image est obligatoire.\n");
            imageTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else if (!imageTextField.getText().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            errorMessage.append("Le format de l'image doit être .jpg, .jpeg, .png ou .gif.\n");
            imageTextField.setStyle(INVALID_STYLE);
            isValid = false;
        }

        // Validation du statut
        if (statusTextField.getText().isEmpty()) {
            errorMessage.append("Le statut est obligatoire.\n");
            statusTextField.setStyle(INVALID_STYLE);
            isValid = false;
        } else if (!statusTextField.getText().matches("(?i)(disponible|vendu|en_exposition)")) {
            errorMessage.append("Le statut doit être 'disponible', 'vendu' ou 'en_exposition'.\n");
            statusTextField.setStyle(INVALID_STYLE);
            isValid = false;
        }

        // Afficher les erreurs s'il y en a
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
        }

        return isValid;
    }

    @FXML
    void creatartwork(ActionEvent event) {
        if (validateFields()) {
            try {
                String titre = titreTextField.getText().trim();
                String description = descriptionTextField.getText().trim();
                int prix = Integer.parseInt(prixTextField.getText());
                String artistenom = artistenomTextField.getText().trim();
                String image = imageTextField.getText().trim();
                String status = statusTextField.getText().toLowerCase();

                artwork artwork = new artwork(titre, description, prix, image, artistenom, status);
                serviceartwork serviceartwork = new serviceartwork();
                serviceartwork.add(artwork);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Artwork ajouté avec succès !");

                // Ask user if they want to return to the list
                Alert returnAlert = new Alert(Alert.AlertType.CONFIRMATION);
                returnAlert.setTitle("Retour à la liste");
                returnAlert.setHeaderText("Voulez-vous retourner à la liste des artworks?");
                returnAlert.setContentText("Cliquez sur 'OK' pour retourner à la liste, ou 'Annuler' pour rester sur cette page.");
                
                Optional<ButtonType> result = returnAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Return to the artwork list
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/listartwork.fxml"));
                        Parent root = loader.load();
                        
                        // Get the current stage
                        Stage stage = (Stage) btnSave.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Liste des Artworks");
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la liste des artworks : " + e.getMessage());
                    }
                } else {
                    // Clear the form for a new entry
                    clearForm();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'artwork : " + e.getMessage());
            }
        }
    }
    
    private void clearForm() {
        titreTextField.clear();
        descriptionTextField.clear();
        prixTextField.clear();
        artistenomTextField.clear();
        imageTextField.clear();
        statusTextField.clear();
        
        // Réinitialiser les styles
        titreTextField.setStyle(DEFAULT_STYLE);
        descriptionTextField.setStyle(DEFAULT_STYLE);
        prixTextField.setStyle(DEFAULT_STYLE);
        artistenomTextField.setStyle(DEFAULT_STYLE);
        imageTextField.setStyle(DEFAULT_STYLE);
        statusTextField.setStyle(DEFAULT_STYLE);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
    


