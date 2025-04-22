package tn.esprit.controllers;

import tn.esprit.models.Commentaire_f;
import tn.esprit.models.Forum;
import tn.esprit.services.ServiceCommentaire_f;
import tn.esprit.services.ServiceForum;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AjouterCommentairesController {

    @FXML
    private ComboBox<String> TFForum;

    @FXML
    private TextField TFtexte;

    @FXML
    private Button ajouterCommentAction;

    @FXML
    private Button voirComments;

    @FXML
    private Label successMessage;
    @FXML
    private Label min;
    @FXML
    private Label bien;

    private ServiceForum serviceForum = new ServiceForum();
    private ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();

    @FXML
    public void initialize() {
        try {
            Set<String> forumTitles = serviceForum.getAllTitles();
            List<String> forumTitleList = new ArrayList<>(forumTitles);
            ObservableList<String> observableForumList = FXCollections.observableArrayList(forumTitleList);
            TFForum.setItems(observableForumList);

            min.setVisible(false);
            bien.setVisible(false);

        } catch (SQLException e) {
            showAlert("Database Error", "Error while fetching forum titles", "An error occurred while fetching the list of forum titles: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterCommentAction(ActionEvent actionEvent) {
        if (!isInputValid()) {
            showAnimatedErrorAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        String selectedTitle = TFForum.getSelectionModel().getSelectedItem();
        String commentText = TFtexte.getText();

        Forum selectedForum = null;
        try {
            selectedForum = serviceForum.getOneByTitle(selectedTitle);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Error fetching forum", "Could not fetch forum details: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (selectedForum != null) {
            Commentaire_f commentaire = new Commentaire_f(1, selectedForum, new Date(System.currentTimeMillis()), commentText);
            try {
                serviceCommentaire.ajouter(commentaire);

                // Show success message instead of navigating
                showAnimatedSuccessAlert("Succès", "Commentaire ajouté avec succès!");

                // Clear the input fields
                TFtexte.clear();
                TFForum.getSelectionModel().clearSelection();


            } catch (Exception e) {
                showAlert("Error", "An unexpected error occurred", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();

            }
        } else {
            showAlert("Forum Error", "Forum not found", "The selected forum could not be found.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void voirComments(ActionEvent actionEvent) {
        System.out.println("Voir la liste des commentaires is clicked");
    }

    private boolean isInputValid() {
        String selectedTitle = TFForum.getSelectionModel().getSelectedItem();
        String commentText = TFtexte.getText();
        return selectedTitle != null && !commentText.isEmpty();
    }

    @FXML
    private void handleTextInput() {
        String commentText = TFtexte.getText().trim();

        if (commentText.length() < 3) {
            min.setVisible(true);
            bien.setVisible(false);
        } else {
            min.setVisible(false);
            bien.setVisible(true);
        }
    }

    // Helper methods

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAnimatedErrorAlert(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT); // Make the stage transparent

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 15; -fx-background-radius: 5;");
        okButton.setOnAction(event -> {
            dialogStage.close();
        });

        VBox dialogVBox = new VBox(10, messageLabel, okButton);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setStyle("-fx-background-color: rgba(231, 76, 60, 0.9); -fx-padding: 20px; -fx-background-radius: 10;");

        Scene dialogScene = new Scene(dialogVBox);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT); // Make the scene transparent
        dialogStage.setScene(dialogScene);

        // Animations
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), dialogVBox);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), dialogVBox);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        fadeTransition.play();
        scaleTransition.play();

        dialogStage.showAndWait();
    }

    private void showAnimatedSuccessAlert(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT); // Make the stage transparent

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 15; -fx-background-radius: 5;");
        okButton.setOnAction(event -> {
            dialogStage.close();
        });

        VBox dialogVBox = new VBox(10, messageLabel, okButton);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setStyle("-fx-background-color: rgba(46, 204, 113, 0.9); -fx-padding: 20px; -fx-background-radius: 10;");

        Scene dialogScene = new Scene(dialogVBox);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT); // Make the scene transparent
        dialogStage.setScene(dialogScene);

        // Animations
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), dialogVBox);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), dialogVBox);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        fadeTransition.play();
        scaleTransition.play();

        dialogStage.showAndWait();
    }


}