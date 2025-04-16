package tn.esprit.controllers;

import tn.esprit.models.Forum;
import tn.esprit.services.ServiceForum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    private Label gooddescription;

    @FXML
    private Label goodtitle;

    @FXML
    private Label warningtitle;

    @FXML
    private TextField TFDescription;

    @FXML
    private TextField TFtitre;

    @FXML
    private Button goafficher;

    @FXML
    private Button ajouterForumAction;

    @FXML
    private ComboBox<String> TFCategorie;

    @FXML
    private ImageView imgView_reclamation;

    @FXML
    private Button uploadbutton;

    private final ServiceForum serviceForum = new ServiceForum();
    private String imagePath = ""; // To store the image path

    java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize ComboBox with choices
        TFCategorie.getItems().addAll(
                "Art-Thérapie et Bien-Être",
                "Techniques Artistiques",
                "Témoignages et Inspirations",
                "Ressources et Outils",
                "Questions et Conseils"
        );

        // Validation on the Description field
        TFDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 12) {
                warningDescription.setVisible(true);
                gooddescription.setVisible(false);
                TFDescription.requestFocus();
            } else {
                warningDescription.setVisible(false);
                gooddescription.setVisible(true);
            }
        });

        // Validation on the Title field
        TFtitre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 4) {
                warningtitle.setVisible(true);
                goodtitle.setVisible(false);
                TFtitre.requestFocus();
            } else {
                warningtitle.setVisible(false);
                goodtitle.setVisible(true);
            }
        });

        // Add listener to ComboBox for Category
        TFCategorie.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                warningComm.setVisible(true);
            } else {
                warningComm.setVisible(false);
            }
        });
    }

    public void ajouterForumAction(ActionEvent actionEvent) {
        try {
            // Vérifier les longueurs minimales des champs
            if (TFDescription.getText().length() < 12 || TFtitre.getText().length() < 4 || TFCategorie.getValue() == null || TFCategorie.getValue().isEmpty()) {
                // Afficher une alerte d'erreur si une condition de longueur minimale n'est pas respectée
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please make sure all fields have the required length or are filled!");
                alert.show();
                return; // Sortir de la méthode car les conditions ne sont pas respectées
            }

            // Add the new Forum with the image path
            serviceForum.ajouter(new Forum(sqlDate, TFtitre.getText(), TFCategorie.getValue(), TFDescription.getText(), imagePath));


            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Forum added successfully!");
            alert.show();

            // Load and display the Forum list view (Assuming there's a view to display all forums)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherForum.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException | SQLException e) {
            // Show error message if an exception occurs
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Handle the image upload button
    public void uploadimg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(uploadbutton.getScene().getWindow());

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath(); // Save the file path for later use
            Image image = new Image("file:///" + imagePath);
            imgView_reclamation.setImage(image); // Display the image in ImageView
        }
    }

    public void goafficherAction(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherForum.fxml")); // Adjust the path if necessary

            // Set the new scene
            Stage stage = (Stage) goafficher.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Erreur lors du chargement de la vue.");
            alert.setTitle("Erreur");
            alert.show();
        }
    }
}
