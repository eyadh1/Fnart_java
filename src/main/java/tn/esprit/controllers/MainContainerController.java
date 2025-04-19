package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class MainContainerController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // Charger la vue Home par défaut
        loadView("Home");
    }
    
    @FXML
    private void handleAddBeneficiaire() {
        loadView("/AddBeneficiaire.fxml");
    }

    @FXML
    private void handleAddDons() {
        loadView("/AddDons.fxml");
    }

    @FXML
    private void handleListeBeneficiaires() {
        loadView("/ListeBeneficiaires.fxml");
    }

    @FXML
    private void handleListeDons() {
        loadView("/ListeDons.fxml");
    }
    
    public void loadView(String viewName) {
        try {
            // Construire le chemin du fichier FXML
            String fxmlPath = "/" + viewName + ".fxml";
            
            // Charger le FXML en utilisant le chemin absolu
            FXMLLoader loader = new FXMLLoader(MainContainerController.class.getResource(fxmlPath));
            Parent view = loader.load();
            
            // Mettre à jour le titre
            updateTitle(viewName);
            
            // Remplacer le contenu actuel
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue " + viewName, e);
        }
    }
    
    private void updateTitle(String viewName) {
        switch (viewName) {
            case "Home":
                titleLabel.setText("Bienvenue dans Fnart");
                break;
            case "AddBeneficiaire":
                titleLabel.setText("Ajouter un bénéficiaire");
                break;
            case "AddDons":
                titleLabel.setText("Ajouter un don");
                break;
            case "ListeBeneficiaires":
                titleLabel.setText("Liste des bénéficiaires");
                break;
            case "ListeDons":
                titleLabel.setText("Liste des dons");
                break;
            default:
                titleLabel.setText("Fnart");
        }
    }
    
    private void showError(String message, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        e.printStackTrace();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 