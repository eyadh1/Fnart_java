package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardController {

    @FXML
    private VBox contentArea;

    @FXML
    public void initialize() {
        // Vérification du chargement du CSS
        try {
            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            System.out.println("CSS file found at: " + cssPath);
        } catch (Exception e) {
            System.err.println("Error loading CSS file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBeneficiaire() {
        try {
            // Charger la nouvelle vue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBeneficiaire.fxml"));
            Parent view = loader.load();
            
            // Récupérer le BorderPane parent
            BorderPane borderPane = (BorderPane) contentArea.getParent();
            if (borderPane != null) {
                borderPane.setCenter(view);
                System.out.println("Navigation vers AddBeneficiaire");
            } else {
                showError("Erreur", "Impossible de trouver le conteneur principal", null);
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue AddBeneficiaire", e);
        }
    }

    @FXML
    private void handleAddDons() {
        try {
            // Charger la nouvelle vue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDons.fxml"));
            Parent view = loader.load();
            
            // Récupérer le BorderPane parent
            BorderPane borderPane = (BorderPane) contentArea.getParent();
            if (borderPane != null) {
                borderPane.setCenter(view);
                System.out.println("Navigation vers AddDons");
            } else {
                showError("Erreur", "Impossible de trouver le conteneur principal", null);
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue AddDons", e);
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

