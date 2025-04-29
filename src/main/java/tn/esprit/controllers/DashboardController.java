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

    private void loadForm(String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }
            Node form = FXMLLoader.load(resource);
            artworkGrid.getChildren().clear(); // Clear existing content
            artworkGrid.getChildren().add(form); // Add the new form
        } catch (IOException e) {
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

