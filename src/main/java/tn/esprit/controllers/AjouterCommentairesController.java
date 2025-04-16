package tn.esprit.controllers;

import tn.esprit.models.CommentaireF;
import tn.esprit.models.Forum;
import tn.esprit.services.ServiceCommentaire_f;
import tn.esprit.services.ServiceForum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AjouterCommentairesController {

    @FXML
    private ComboBox<String> TFForum;  // Changed to ComboBox of String (titles)

    @FXML
    private TextField TFtexte;

    @FXML
    private Button ajouterCommentAction;

    @FXML
    private Button voirComments;

    @FXML
    private Label successMessage; // Label for success message
    @FXML
    private Label min; // Label for minimum length validation
    @FXML
    private Label bien; // Label for validation success

    private ServiceForum serviceForum = new ServiceForum();
    private ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();

    // Initialize the controller by populating the ComboBox with forum titles
    @FXML
    public void initialize() {
        try {
            // Fetch all forum titles from the database
            Set<String> forumTitles = serviceForum.getAllTitles(); // Get all titles

            // Convert Set<String> to List<String>
            List<String> forumTitleList = new ArrayList<>(forumTitles);

            // Populate ComboBox with forum titles
            ObservableList<String> observableForumList = FXCollections.observableArrayList(forumTitleList);
            TFForum.setItems(observableForumList);

            // Set the initial state of the validation labels
            min.setVisible(false);
            bien.setVisible(false);

        } catch (SQLException e) {
            // Handle SQLException and show an alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error while fetching forum titles");
            alert.setContentText("An error occurred while fetching the list of forum titles: " + e.getMessage());
            alert.showAndWait();

            // Optionally log the exception for further debugging
            e.printStackTrace();
        }
    }

    // Action when the "Ajouter" button is clicked
    @FXML
    public void ajouterCommentAction(ActionEvent actionEvent) {
        // Validate the input fields
        if (!isInputValid()) {
            // Show an error alert if the validation fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un forum et saisir un texte pour le commentaire.");
            alert.showAndWait();
            return;
        }

        // Get the selected forum title from ComboBox (instead of Forum object)
        String selectedTitle = TFForum.getSelectionModel().getSelectedItem();
        // Get the text from TextField
        String commentText = TFtexte.getText();

        // Find the Forum object based on the selected title
        Forum selectedForum = null;
        try {
            selectedForum = serviceForum.getOneByTitle(selectedTitle);  // Fetch the full Forum object by title
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create a new Commentaire_f object and add it to the database
        if (selectedForum != null) {
            CommentaireF commentaire = new CommentaireF(1, selectedForum, new Date(System.currentTimeMillis()), commentText);
            serviceCommentaire.ajouter(commentaire);

            // Show success message


            // Load the 'AfficherCommentaire.fxml' page after adding the comment
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaire.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de l'affichage de la page des commentaires.");
            }
        }
    }


    // Action when the "Voir la liste des commentaires" button is clicked
    @FXML
    public void voirComments(ActionEvent actionEvent) {
        // Here you can implement functionality to display comments, if necessary.
        System.out.println("Voir la liste des commentaires is clicked");
    }

    // Validate that a forum is selected and a comment is entered
    private boolean isInputValid() {
        String selectedTitle = TFForum.getSelectionModel().getSelectedItem();
        String commentText = TFtexte.getText();

        if (selectedTitle == null || commentText.isEmpty()) {
            // Return false if no forum title is selected or comment is empty
            return false;
        }
        return true;
    }

    // Handle text input validation for minimum length of comment
    @FXML
    private void handleTextInput() {
        String commentText = TFtexte.getText().trim(); // Récupérer le texte du champ

        if (commentText.length() < 3) {
            min.setVisible(true);  // Afficher le message d'erreur
            bien.setVisible(false); // Cacher le message de validation
        } else {
            min.setVisible(false);  // Cacher le message d'erreur
            bien.setVisible(true);  // Afficher le message de validation
        }
    }


    public void recccccccccccccccccccccccc(ActionEvent actionEvent) {
    }

    public void gotoajoutercomment(ActionEvent actionEvent) {
    }
}
