package controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AddBeneficiaire.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter un beneficiaire");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}