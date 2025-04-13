package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

import java.awt.event.ActionEvent;
import java.util.List;

public class listartwork {

    @FXML
    private TableView<artwork> tableView; // Ajout nécessaire

    @FXML
    private TableColumn<artwork, String> titreTableColumn;

    @FXML
    private TableColumn<artwork, String> artistenomTableColumn;

    @FXML
    private TableColumn<artwork, String> descriptionTableColumn;

    @FXML
    private TableColumn<artwork, Integer> prixTableColumn;

    @FXML
    private TableColumn<artwork, String> statusTableColumn;

    @FXML
    private TableColumn<artwork, String> imageTableCollumn;

    serviceartwork service = new serviceartwork();

    @FXML
    public void initialize() {
        // Associer les colonnes aux attributs de l'entité
        titreTableColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        artistenomTableColumn.setCellValueFactory(new PropertyValueFactory<>("artistenom"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixTableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageTableCollumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Récupérer et afficher les données
        List<artwork> artworks = service.getAll(); // méthode corrigée dans service
        tableView.setItems(FXCollections.observableArrayList(artworks));
    }

    @FXML
    private void btnupdate() {
        artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            try {
                // Charger le fichier FXML de modification avec le bon chemin
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/modifierartwork.fxml"));

                Parent root = loader.load();
                modifierartworkController controller = loader.getController();
                controller.setArtwork(selectedArtwork);

                // Créer et afficher la nouvelle scène
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la fenêtre de modification.");
            }
        } else {
            showAlert("Sélectionner une œuvre", "Veuillez sélectionner une œuvre à modifier.");
        }
    }
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
