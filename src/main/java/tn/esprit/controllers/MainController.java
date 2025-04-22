package tn.esprit.controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {

    @FXML
    private Button ajouterCommentaireButton;

    @FXML
    private Button autreBouton;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button forumsButton; // Inject the new button

    @FXML
    public void initialize() {
        // You can perform initializations here if necessary.
    }

    @FXML
    public void afficherFormulaireCommentaire(ActionEvent event) {
        loadViewWithAnimation("/AjouterCommentaires.fxml");
    }

    @FXML
    public void afficherMesCommentaires(ActionEvent event) {
        loadViewWithAnimation("/AfficherCommentaireContainers.fxml");
    }

    @FXML
    public void afficherChatBot(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatBotTache.fxml"));
            Parent view = loader.load();



            FadeTransition ft = new FadeTransition(Duration.millis(300), view);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);

            ScaleTransition st = new ScaleTransition(Duration.millis(300), view);
            st.setFromX(0.7);
            st.setFromY(0.7);
            st.setToX(1.0);
            st.setToY(1.0);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Jouer les animations en séquence
            ParallelTransition pt = new ParallelTransition(ft, st);
            SequentialTransition seq = new SequentialTransition(pt);
            seq.play();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du chatbot : " + e.getMessage());
        }
    }


    @FXML
    public void afficherForums(ActionEvent event) {
        loadViewWithAnimation("/ForumClient.fxml"); // Path to your forum view
    }

    private void loadViewWithAnimation(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Animation de fondu
            FadeTransition ft = new FadeTransition(Duration.millis(300), view);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);

            // Animation de mise à l'échelle
            ScaleTransition st = new ScaleTransition(Duration.millis(300), view);
            st.setFromX(0.8);
            st.setFromY(0.8);
            st.setToX(1.0);
            st.setToY(1.0);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Jouer les animations en parallèle
            ft.play();
            st.play();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue : " + e.getMessage());
        }
    }
}