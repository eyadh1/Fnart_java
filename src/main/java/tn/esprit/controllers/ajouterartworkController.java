package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class ajouterartworkController {

    private static final String UPLOAD_DIR = "C:/xampp/htdocs/artwork_images/";
    private AdminDashboardController parentController;

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

    private File selectedImageFile;
    private final serviceartwork artworkService = new serviceartwork();

    @FXML
    public void initialize() {
        imagePreview.setVisible(false);

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer le dossier : " + e.getMessage());
        }
    }

    // === Méthode pour uploader et afficher une image ===
    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
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
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher l'image : " + e.getMessage());
            }
        }
    }

    // === Méthode pour créer l'œuvre et copier l'image ===
    @FXML
    private void creatartwork() {
        try {
            // Générer un nom unique
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalFileName = selectedImageFile.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = timestamp + "_" + originalFileName;

            // Copier l'image sous htdocs
            Path targetPath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'objet Artwork
            Artwork newArtwork = new Artwork(
                    titreTextField.getText(),
                    descriptionTextField.getText(),
                    Integer.parseInt(prixTextField.getText()),
                    fileName,
                    artistenomTextField.getText()
            );

            artworkService.add(newArtwork);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Œuvre ajoutée avec succès !");
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();

            if (parentController != null) {
                parentController.refreshArtworkTable();
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème : " + e.getMessage());
        }
    }

    // === Méthode d'affichage des alertes ===
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // === Setter pour le parent controller ===
    public void setParentController(AdminDashboardController controller) {
        this.parentController = controller;
    }

    private VBox createSimpleShareOption(String label, String icon, javafx.event.EventHandler<javafx.scene.input.MouseEvent> handler) {
        VBox optionBox = new VBox(2);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPadding(new Insets(8));
        optionBox.setStyle("-fx-background-color: #f5f6fa; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");

        optionBox.getChildren().addAll(iconLabel, textLabel);
        optionBox.setOnMouseClicked(handler);

        // Optionnel : effet au survol
        optionBox.setOnMouseEntered(e -> optionBox.setStyle(optionBox.getStyle() + "-fx-background-color: #e1e1e1;"));
        optionBox.setOnMouseExited(e -> optionBox.setStyle(optionBox.getStyle().replace("-fx-background-color: #e1e1e1;", "-fx-background-color: #f5f6fa;")));

        return optionBox;
    }
}
