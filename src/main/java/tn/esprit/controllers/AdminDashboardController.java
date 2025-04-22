package tn.esprit.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

public class AdminDashboardController {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button forumButton;

    @FXML
    private Button commentButton;

    @FXML
    private Button statsButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton; // New Logout Button


    @FXML
    public void showForumList(ActionEvent event) {
        try {
            FXMLLoader forumLoader = new FXMLLoader(getClass().getResource("/AfficherForum.fxml"));
            Parent forumView = forumLoader.load();
            AfficherForumController forumController = forumLoader.getController();

            FXMLLoader addForumLoader = new FXMLLoader(getClass().getResource("/AjouterForum.fxml"));
            Parent addForumView = addForumLoader.load();
            AjouterForumController addForumController = addForumLoader.getController();

            addForumController.setAfficherForumController(forumController);

            VBox container = new VBox();
            container.getChildren().addAll(addForumView, forumView);

            ScrollPane scrollPane = new ScrollPane(container);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setPannable(true);

            setContentWithAnimation(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showCommentList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaire.fxml"));
            Parent commentView = loader.load();

            setContentWithAnimation(commentView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stat.fxml"));
            Parent statsView = loader.load();

            setContentWithAnimation(statsView);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, possibly showing an alert to the user.
        }
    }

    @FXML
    void logout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter?");
        alert.setContentText("Cliquez sur OK pour vous déconnecter ou Annuler pour rester connecté.");

        // Customizing the alert (optional, make it "attractive")
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/admin-style.css").toExternalForm()); // Create alert-style.css
        dialogPane.getStyleClass().add("myAlert");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            // Load the login view
            try {
                Parent loginView = FXMLLoader.load(getClass().getResource("/Login.fxml")); // Adjust path if needed
                contentArea.getChildren().setAll(loginView); // Replace content in the content area
                AnchorPane.setTopAnchor(loginView, 0.0);
                AnchorPane.setBottomAnchor(loginView, 0.0);
                AnchorPane.setLeftAnchor(loginView, 0.0);
                AnchorPane.setRightAnchor(loginView, 0.0);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the error of not being able to load login view.
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Impossible de charger l'écran de connexion.");
                errorAlert.setContentText("Une erreur s'est produite lors du chargement de l'interface de connexion.  Consultez les journaux pour plus d'informations.");
                errorAlert.showAndWait();
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void setContentWithAnimation(Parent node) {
        // Clear current content
        contentArea.getChildren().clear();

        // Set anchors to stretch the content
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);

        // Add the new content with fade-in animation
        node.setOpacity(0);
        contentArea.getChildren().add(node);

        // Create fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void setContent(Parent node) {
        contentArea.getChildren().clear();
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        contentArea.getChildren().add(node);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize animations here
        animateWelcomeMessage();
    }

    private void animateWelcomeMessage() {
        // Fade Transition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), welcomeLabel);
        fadeTransition.setFromValue(0.0);  // Start fully transparent
        fadeTransition.setToValue(1.0);    // Fade in to fully visible
        fadeTransition.play();

        // Scale Transition (Optional, adds a zoom effect)
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), welcomeLabel);
        scaleTransition.setFromX(0.5); // Start at half size (X-axis)
        scaleTransition.setFromY(0.5); // Start at half size (Y-axis)
        scaleTransition.setToX(1.0);   // Scale to normal size (X-axis)
        scaleTransition.setToY(1.0);   // Scale to normal size (Y-axis)
        scaleTransition.play();
    }
}