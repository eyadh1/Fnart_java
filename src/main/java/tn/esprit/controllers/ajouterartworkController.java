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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;

public class ajouterartworkController {

    private static final String VALID_STYLE = "-fx-border-color: #00ff00;";
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";
    private static final String DEFAULT_STYLE = "";
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";
    private File selectedFile;

    @FXML
    private Text ArtworkText;

    @FXML
    private TextField artistenomTextField;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpload;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private ImageView imagePreview;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField titreTextField;

    @FXML
    void initialize() {
        setupValidationListeners();
        // Create uploads directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @FXML
    void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());
        if (selectedFile != null) {
            // Update TextField with file name
            imageTextField.setText(selectedFile.getName());
            
            // Show image preview
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
            
            validateImageField(selectedFile.getName());
        }
    }

    private String saveImage() throws IOException {
        if (selectedFile != null) {
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path destination = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }
        return null;
    }

    @FXML
    void creatartwork(ActionEvent event) {
        if (validateFields()) {
            try {
                String imagePath = saveImage();
                if (imagePath == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une image.");
                    return;
                }

                artwork newArtwork = new artwork(
                    titreTextField.getText(),
                    descriptionTextField.getText(),
                    Integer.parseInt(prixTextField.getText()),
                    imagePath,
                    artistenomTextField.getText(),
                    statusTextField.getText()
                );

                serviceartwork service = new serviceartwork();
                service.add(newArtwork);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'œuvre d'art a été ajoutée avec succès!");

                // Demander à l'utilisateur s'il veut voir la liste
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Voir la liste");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Voulez-vous voir la liste des œuvres d'art ?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/listartwork.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) btnSave.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la liste : " + e.getMessage());
                    }
                } else {
                    clearForm();
                }

            } catch (SQLException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'œuvre: " + e.getMessage());
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
        imagePreview.setImage(null);
        selectedFile = null;

        // Reset styles
        titreTextField.setStyle(DEFAULT_STYLE);
        descriptionTextField.setStyle(DEFAULT_STYLE);
        prixTextField.setStyle(DEFAULT_STYLE);
        artistenomTextField.setStyle(DEFAULT_STYLE);
        imageTextField.setStyle(DEFAULT_STYLE);
        statusTextField.setStyle(DEFAULT_STYLE);
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

    private void validateImageField(String text) {
        if (text.isEmpty()) {
            imageTextField.setStyle(INVALID_STYLE);
        } else if (text.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            imageTextField.setStyle(VALID_STYLE);
        } else {
            imageTextField.setStyle(INVALID_STYLE);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
    


