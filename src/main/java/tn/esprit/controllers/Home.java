package tn.esprit.controllers;

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
// In the navigateToLogin method or wherever you're loading the login scene


            try {
                Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));

                Scene scene = new Scene(root);
                // Add CSS file
                scene.getStylesheets().add(getClass().getResource("/styles/AdminDashboard.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage()
                );
            }
    }
}