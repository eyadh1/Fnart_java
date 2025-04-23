package tn.esprit.controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Ajouter le CSS avec un chemin absolu
            String cssPath = "/styles/frontartwork.css";
            var cssResource = getClass().getResource(cssPath);
            if (cssResource == null) {
                System.err.println("Could not find CSS file: " + cssPath);
            } else {
                scene.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("CSS loaded successfully: " + cssPath);
            }
            
            stage.setTitle("Art Therapy Gallery");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
