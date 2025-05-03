package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.TilePane;
import com.stripe.Stripe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private TilePane artworkGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vérification du chargement du CSS
        try {
            String cssPath = getClass().getResource("/css/pinterest-style.css").toExternalForm();
            System.out.println("CSS file found at: " + cssPath);
        } catch (Exception e) {
            System.err.println("Error loading CSS file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBeneficiaire() {
        loadForm("/AddBeneficiaire.fxml");
    }

    @FXML
    private void handleAddDons() {
        loadForm("/AddDons.fxml");
    }

    @FXML
    private void handleAtelier() {
        // Revert to loading FrontendAtelier.fxml
        loadForm("/FrontendAtelier.fxml");
    }

    private void loadForm(String fxmlFile) {
        try {
            System.out.println("Attempting to load FXML file: " + fxmlFile);
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }
            System.out.println("FXML file found at: " + resource.toExternalForm());
            Node form = FXMLLoader.load(resource);
            artworkGrid.getChildren().clear(); // Clear existing content
            artworkGrid.getChildren().add(form); // Add the new form
            System.out.println("Successfully loaded and displayed FXML file: " + fxmlFile);
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la vue " + fxmlFile, e);
        }
    }

    private void showError(String title, String content, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content + (e != null ? "\n" + e.getMessage() : ""));
        alert.showAndWait();
    }
}

