package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class DashboardController {

    @FXML
    private ImageView featuredArtwork;

    @FXML
    private Label artworkTitle;

    @FXML
    private Label artworkDesc;

    @FXML
    private ListView<String> artworkList;

    @FXML
    public void initialize() {
        // Verify CSS loading
        try {
            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            System.out.println("CSS file found at: " + cssPath);
        } catch (Exception e) {
            System.err.println("Error loading CSS file: " + e.getMessage());
            e.printStackTrace();
        }

        featuredArtwork.setImage(new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Vincent_Willem_van_Gogh_128.jpg/800px-Vincent_Willem_van_Gogh_128.jpg"));
        artworkTitle.setText("Starry Night by Van Gogh");
        artworkDesc.setText("A masterpiece often used in therapeutic discussions.");
        
        artworkList.getItems().addAll(
            "Calm Horizon by A. Smith",
            "Silent Forest by L. Wong",
            "Urban Escape by M. Garcia",
            "Warm Breeze by J. Kim"
        );
    }
    @FXML
    private void handleAddBeneficiaire() {
        try {
            // Charger la vue AddBeneficiaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBeneficiaire.fxml"));
            Parent view = loader.load();

            // Remplacer le contenu actuel
            // Cette méthode sera appelée par le MainContainerController
            System.out.println("Navigation vers AddBeneficiaire");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue AddBeneficiaire", e);
        }
    }

    @FXML
    private void handleAddDons() {
        try {
            // Charger la vue AddDons
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDons.fxml"));
            Parent view = loader.load();

            // Remplacer le contenu actuel
            // Cette méthode sera appelée par le MainContainerController
            System.out.println("Navigation vers AddDons");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue AddDons", e);
        }
    }

    @FXML
    private void handleListeBeneficiaire() {
        try {
            // Charger la vue ListeBeneficiaires
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeBeneficiaires.fxml"));
            Parent view = loader.load();

            // Remplacer le contenu actuel
            // Cette méthode sera appelée par le MainContainerController
            System.out.println("Navigation vers ListeBeneficiaires");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue ListeBeneficiaires", e);
        }
    }

    @FXML
    private void handleListeDons() {
        try {
            // Charger la vue ListeDons
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDons.fxml"));
            Parent view = loader.load();

            // Remplacer le contenu actuel
            // Cette méthode sera appelée par le MainContainerController
            System.out.println("Navigation vers ListeDons");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue ListeDons", e);
        }
    }

    private void showError(String title, String content, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content + "\n" + e.getMessage());
        alert.showAndWait();
    }
}

