package controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Home extends Application {
    private Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Fnart - Gestion des Dons");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private void handleAddBeneficiaire() {
        try {
            Stage stage = new Stage();
            AddBeneficiaireController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la fenêtre d'ajout de bénéficiaire: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddDons() {
        try {
            Stage stage = new Stage();
            AddDonsController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la fenêtre d'ajout de don: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleListeBeneficiaire() {
        try {
            Stage stage = new Stage();
            ListeBeneficiairesController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la liste des bénéficiaires: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleListeDons() {
        try {
            Stage stage = new Stage();
            ListeDonsController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la liste des dons: " + e.getMessage());
            alert.showAndWait();
        }
    }
}