package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.models.Commande;
import tn.esprit.services.ServiceCommande;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.Date;

public class listcommandeController {

    @FXML
    private TableView<Commande> tableView;

    @FXML
    private TableColumn<Commande, String> nomTableColumn;

    @FXML
    private TableColumn<Commande, String> adressTableColumn;

    @FXML
    private TableColumn<Commande, String> telephoneTableColumn;

    @FXML
    private TableColumn<Commande, String> emailTableColumn;

    @FXML
    private TableColumn<Commande, Date> dateTableColumn;

    @FXML
    private TableColumn<Commande, Double> totaleTableColumn;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnRetour;

    private ServiceCommande service = new ServiceCommande();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        nomTableColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        adressTableColumn.setCellValueFactory(new PropertyValueFactory<>("adress"));
        telephoneTableColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        totaleTableColumn.setCellValueFactory(new PropertyValueFactory<>("totale"));

        // Chargement des données
        refreshTable();
    }

    @FXML
    private void btnupdate(ActionEvent event) {
        Commande selectedCommande = tableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommande.fxml"));
                Parent root = loader.load();

                modifiercommandeController controller = loader.getController();
                controller.setParentController(this);
                controller.setCommande(selectedCommande);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Commande - " + selectedCommande.getId());
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Fichier FXML non trouvé: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur inattendue: " + e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une commande à modifier.");
        }
    }

    @FXML
    private void deletecmd(ActionEvent event) {
        Commande selectedCommande = tableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la commande N°" + selectedCommande.getId() + "?");

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                try {
                    service.delete(selectedCommande);
                    tableView.getItems().remove(selectedCommande);
                    showAlert("Succès", "La commande a été supprimée avec succès.");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une commande à supprimer.");
        }
    }

    @FXML
    private void retourArtworkList(ActionEvent event) {
        try {
            // Charger la vue de la liste des artworks
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listartwork.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            
            // Changer la scène
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Artworks");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des artworks : " + e.getMessage());
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshTable() {
        List<Commande> commandes = service.getAll();
        tableView.setItems(FXCollections.observableArrayList(commandes));
    }
} 