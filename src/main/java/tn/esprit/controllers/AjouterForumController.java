package tn.esprit.controllers;

import tn.esprit.models.Forum;
import tn.esprit.models.User;
import tn.esprit.services.ServiceForum;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class AjouterForumController implements Initializable {

    @FXML
    private Label warningDescription;

    @FXML
    private Label warningComm;

    @FXML
    private Label warningtitle;

    @FXML
    private TextField TFtitre;

    @FXML
    private TextArea TFDescription;

    @FXML
    private Button ajouterForumAction;

    @FXML
    private ComboBox<String> TFCategorie;

    @FXML
    private ImageView imgView_reclamation;

    @FXML
    private Button uploadbutton;

    private final ServiceForum serviceForum = new ServiceForum();
    private String imagePath = "";
    private AfficherForumController afficherForumController;

    java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TFCategorie.getItems().addAll(
                "Art-Thérapie et Bien-Être",
                "Techniques Artistiques",
                "Témoignages et Inspirations",
                "Ressources et Outils",
                "Questions et Conseils"
        );

        TFDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 12) {
                showWarning(warningDescription, true);
            } else {
                showWarning(warningDescription, false);
            }
        });

        TFtitre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 4) {
                showWarning(warningtitle, true);
            } else {
                showWarning(warningtitle, false);
            }
        });

        TFCategorie.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                showWarning(warningComm, true);
            } else {
                showWarning(warningComm, false);
            }
        });

        animateButton(ajouterForumAction);
        animateButton(uploadbutton);
    }

    private void showWarning(Label label, boolean show) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), label);
        ft.setFromValue(show ? 0.0 : 1.0);
        ft.setToValue(show ? 1.0 : 0.0);
        ft.play();
        label.setVisible(show);
    }

    private void animateButton(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    public void ajouterForumAction(ActionEvent actionEvent) {
        // Vérifier les longueurs minimales des champs
        if (TFDescription.getText().length() < 12 || TFtitre.getText().length() < 4 || TFCategorie.getValue() == null || TFCategorie.getValue().isEmpty()) {
            showAnimatedErrorAlert("Erreur de validation", "Veuillez vous assurer que :\n- Le titre contient au moins 4 caractères\n- La description contient au moins 12 caractères\n- Une catégorie est sélectionnée");
            return;
        }

        try {
            // Créer un nouvel utilisateur (à remplacer par l'utilisateur connecté)
            User currentUser = new User(1); // Assurez-vous que cet ID existe dans votre base de données
            
            // Créer le nouveau forum avec l'utilisateur
            Forum newForum = new Forum(sqlDate, TFtitre.getText(), currentUser, TFCategorie.getValue(), TFDescription.getText(), imagePath);
            
            // Ajouter le forum
            serviceForum.ajouter(newForum);

            // Recharger la liste des forums
            if (afficherForumController != null) {
                afficherForumController.loadForumList();
            }

            // Réinitialiser le formulaire
            TFtitre.clear();
            TFDescription.clear();
            TFCategorie.setValue(null);
            imgView_reclamation.setImage(null);
            imagePath = "";

            // Afficher un message de succès
            showSuccessMessage(actionEvent);

        } catch (SQLException e) {
            String errorMessage;
            if (e.getMessage().contains("L'utilisateur avec l'ID")) {
                errorMessage = "Erreur : Aucun utilisateur trouvé dans la base de données.\nVeuillez vous connecter ou créer un compte d'abord.";
            } else if (e.getMessage().contains("foreign key constraint fails")) {
                errorMessage = "Erreur : Problème avec l'ID utilisateur.\nVeuillez vous reconnecter.";
            } else {
                errorMessage = "Erreur lors de l'ajout du forum : " + e.getMessage();
            }
            showAnimatedErrorAlert("Erreur", errorMessage);
            e.printStackTrace();
        }
    }

    private void showSuccessMessage(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Forum Créé avec Succès !");
        alert.setContentText("Votre forum a été ajouté avec succès.");
        alert.show();
    }

    public void uploadimg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(uploadbutton.getScene().getWindow());

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image("file:///" + imagePath);
            imgView_reclamation.setImage(image);
        }
    }

    public void setAfficherForumController(AfficherForumController afficherForumController) {
        this.afficherForumController = afficherForumController;
    }

    @FXML
    void addForum(ActionEvent event) {  // Assuming this is your add forum method
        // ... save your forum to the database ...

        // Refresh the forum list:
        if (afficherForumController != null) {
            afficherForumController.loadForumList();
        } else {
            System.err.println("afficherForumController is null.  Forum list may not refresh.");
        }

        // ... any other post-save operations ...
    }

    // Helper method to show regular alerts
    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper method to show the animated error alert
    private void showAnimatedErrorAlert(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);

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
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(dialogScene);

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