package tn.esprit.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import tn.esprit.utils.ProtocolHandler;

import java.io.IOException;
import java.util.List;

public class Home extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        // Handle any protocol links that were used to start the application
        Parameters params = getParameters();
        List<String> raw = params.getRaw();
        if (!raw.isEmpty()) {
            String url = raw.get(0);
            if (url.startsWith("fnart://")) {
                Platform.runLater(() -> ProtocolHandler.handleProtocol(url));
                return;
            }
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root);
            // Add CSS file
            scene.getStylesheets().add(getClass().getResource("/styles/AdminDashboard.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}