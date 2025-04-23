package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Artwork;
import tn.esprit.services.serviceartwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class ajouterartworkController {

    private static final String UPLOAD_DIR = "C:/xampp/htdocs/artwork_images/";

    @FXML
    private TextField titreTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField prixTextField;
    @FXML
    private TextField artistenomTextField;
    @FXML
    private TextField imageTextField;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnSave;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Text ArtworkText;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox formVBox;

    private File selectedImageFile;
    private AdminDashboardController parentController;
    private final serviceartwork artworkService = new serviceartwork();

    @FXML
    public void initialize() {
        // Initialize components
        imagePreview.setVisible(false);
        
        // Create uploads directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer le dossier uploads : " + e.getMessage());
        }
        
        applyStyles();

        // Ajouter les écouteurs de validation en temps réel
        setupValidationListeners();
    }

    private void applyStyles() {
        // Style pour le conteneur principal
        if (rootPane != null) {
            rootPane.setStyle(
                "-fx-background-color: #f5f6fa; " +
                "-fx-padding: 20px;"
            );
        }

        // Style pour le titre principal
        if (ArtworkText != null) {
            ArtworkText.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold; " +
                "-fx-fill: #2c3e50;"
            );
        }

        // Style pour tous les labels
        if (rootPane != null) {
            rootPane.lookupAll("Label").forEach(node -> {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    label.setStyle(
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #34495e; " +
                        "-fx-font-weight: 600; " +
                        "-fx-padding: 5px 0;"
                    );
                }
            });
        }

        // Style pour tous les champs de texte
        String textFieldStyle =
            "-fx-min-height: 35px; " +
            "-fx-font-size: 14px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #dcdde1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-prompt-text-fill: #95a5a6;";

        // Appliquer le style à chaque TextField individuellement
        if (titreTextField != null) titreTextField.setStyle(textFieldStyle);
        if (descriptionTextField != null) {
            descriptionTextField.setStyle(textFieldStyle);
            descriptionTextField.setPrefHeight(80);
        }
        if (prixTextField != null) prixTextField.setStyle(textFieldStyle);
        if (artistenomTextField != null) artistenomTextField.setStyle(textFieldStyle);
        if (imageTextField != null) imageTextField.setStyle(textFieldStyle);

        // Style pour le bouton de parcourir
        if (btnUpload != null) {
            btnUpload.setStyle(
                "-fx-background-color: #ecf0f1; " +
                "-fx-text-fill: #2c3e50; " +
                "-fx-font-size: 14px; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 8px 16px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-cursor: hand;"
            );

            btnUpload.setOnMouseEntered(e ->
                btnUpload.setStyle(btnUpload.getStyle() + "-fx-background-color: #dcdde1;")
            );

            btnUpload.setOnMouseExited(e ->
                btnUpload.setStyle(btnUpload.getStyle().replace("-fx-background-color: #dcdde1;", "-fx-background-color: #ecf0f1;"))
            );
        }

        // Style pour le bouton enregistrer
        if (btnSave != null) {
            btnSave.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px 20px; " +
                "-fx-min-width: 120px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"
            );

            btnSave.setOnMouseEntered(e ->
                btnSave.setStyle(btnSave.getStyle() + "-fx-background-color: #2980b9;")
            );

            btnSave.setOnMouseExited(e ->
                btnSave.setStyle(btnSave.getStyle().replace("-fx-background-color: #2980b9;", "-fx-background-color: #3498db;"))
            );

            btnSave.setOnMousePressed(e ->
                btnSave.setStyle(btnSave.getStyle() + "-fx-scale-x: 0.98; -fx-scale-y: 0.98;")
            );

            btnSave.setOnMouseReleased(e ->
                btnSave.setStyle(btnSave.getStyle().replace("-fx-scale-x: 0.98; -fx-scale-y: 0.98;", ""))
            );
        }

        // Style pour la prévisualisation d'image
        if (imagePreview != null) {
            imagePreview.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);"
            );
        }
    }

    private void setupValidationListeners() {
        // Validation du titre (entre 3 et 50 caractères)
        titreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                titreTextField.setText(oldValue);
            }
            validateTitle(newValue);
        });

        // Validation du prix (nombres uniquement)
        prixTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                prixTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            validatePrice(newValue);
        });

        // Validation du nom de l'artiste
        artistenomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                artistenomTextField.setText(oldValue);
            }
            validateArtistName(newValue);
        });

        // Validation de la description
        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                descriptionTextField.setText(oldValue);
            }
            validateDescription(newValue);
        });
    }

    private boolean validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            setFieldError(titreTextField, "Le titre est obligatoire");
            return false;
        }
        if (title.length() < 3) {
            setFieldError(titreTextField, "Le titre doit contenir au moins 3 caractères");
            return false;
        }
        if (title.length() > 50) {
            setFieldError(titreTextField, "Le titre ne doit pas dépasser 50 caractères");
            return false;
        }
        removeFieldError(titreTextField);
        return true;
    }

    private boolean validatePrice(String price) {
        if (price == null || price.trim().isEmpty()) {
            setFieldError(prixTextField, "Le prix est obligatoire");
            return false;
        }
        try {
            int priceValue = Integer.parseInt(price);
            if (priceValue <= 0) {
                setFieldError(prixTextField, "Le prix doit être supérieur à 0");
                return false;
            }
        } catch (NumberFormatException e) {
            setFieldError(prixTextField, "Le prix doit être un nombre valide");
            return false;
        }
        removeFieldError(prixTextField);
        return true;
    }

    private boolean validateArtistName(String name) {
        if (name == null || name.trim().isEmpty()) {
            setFieldError(artistenomTextField, "Le nom de l'artiste est obligatoire");
            return false;
        }
        if (name.length() < 2) {
            setFieldError(artistenomTextField, "Le nom de l'artiste doit contenir au moins 2 caractères");
            return false;
        }
        if (name.length() > 100) {
            setFieldError(artistenomTextField, "Le nom de l'artiste ne doit pas dépasser 100 caractères");
            return false;
        }
        if (!name.matches("^[\\p{L} .'-]+$")) {
            setFieldError(artistenomTextField, "Le nom de l'artiste ne doit contenir que des lettres");
            return false;
        }
        removeFieldError(artistenomTextField);
        return true;
    }

    private boolean validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            setFieldError(descriptionTextField, "La description est obligatoire");
            return false;
        }
        if (description.length() < 10) {
            setFieldError(descriptionTextField, "La description doit contenir au moins 10 caractères");
            return false;
        }
        if (description.length() > 500) {
            setFieldError(descriptionTextField, "La description ne doit pas dépasser 500 caractères");
            return false;
        }
        removeFieldError(descriptionTextField);
        return true;
    }

    private boolean validateImage() {
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une image");
            return false;
        }
        String fileName = selectedImageFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier doit être une image (JPG, JPEG ou PNG)");
            return false;
        }
        if (selectedImageFile.length() > 5 * 1024 * 1024) { // 5 MB
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'image ne doit pas dépasser 5 MB");
            return false;
        }
        return true;
    }

    private void setFieldError(Control field, String message) {
        field.setStyle("-fx-border-color: red;");
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(field, tooltip);
    }

    private void removeFieldError(Control field) {
        field.setStyle("");
        Tooltip.uninstall(field, field.getTooltip());
    }

    public void setParentController(AdminDashboardController controller) {
        this.parentController = controller;
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );

        File file = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());

        if (file != null) {
            selectedImageFile = file;
            imageTextField.setText(file.getName());

            try {
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                imagePreview.setVisible(true);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void creatartwork() {
        if (!validateAllFields()) {
            return;
        }

        try {
            // Generate unique filename with timestamp and clean original filename
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalFileName = selectedImageFile.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = timestamp + "_" + originalFileName;
            
            // Copy image to htdocs directory
            Path targetPath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Create new artwork object with the web-accessible path
            Artwork newArtwork = new Artwork(
                titreTextField.getText().trim(),
                descriptionTextField.getText().trim(),
                Integer.parseInt(prixTextField.getText().trim()),
                fileName, // Store only the filename, not the full path
                artistenomTextField.getText().trim()
            );

            // Save to database
            artworkService.add(newArtwork);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'œuvre d'art a été ajoutée avec succès!");

            // Refresh parent controller if available
            if (parentController != null) {
                parentController.refreshArtworkTable();
            }

            // Close the window
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre valide.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de copier l'image : " + e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout dans la base de données : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    private boolean validateAllFields() {
        boolean isValid = true;
        
        isValid &= validateTitle(titreTextField.getText());
        isValid &= validateDescription(descriptionTextField.getText());
        isValid &= validatePrice(prixTextField.getText());
        isValid &= validateArtistName(artistenomTextField.getText());
        isValid &= validateImage();

        return isValid;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}