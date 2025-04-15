package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;
import javafx.util.Callback;

import javafx.event.ActionEvent;
import java.util.List;

public class listartwork {

    @FXML
    private TableView<artwork> tableView;

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

    @FXML
    private TableColumn<artwork, Void> commanderTableColumn;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnVisualiser;

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

        // Ajouter le bouton Commander dans chaque ligne
        addCommanderButton();

        // Style du bouton visualiser
        btnVisualiser.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
        btnVisualiser.setTooltip(new Tooltip("Voir toutes les commandes"));

        // Récupérer et afficher les données
        List<artwork> artworks = service.getAll();
        tableView.setItems(FXCollections.observableArrayList(artworks));
    }

    private void addCommanderButton() {
        Callback<TableColumn<artwork, Void>, TableCell<artwork, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<artwork, Void> call(final TableColumn<artwork, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Commander");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            artwork artwork = getTableView().getItems().get(getIndex());
                            commanderArtwork(artwork);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        commanderTableColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void visualiserToutesCommandes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcommande.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste de toutes les commandes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des commandes : " + e.getMessage());
        }
    }

    private void commanderArtwork(artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutercommande.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer l'artwork
            ajoutercommandeController controller = loader.getController();
            controller.initData(artwork);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Commander - " + artwork.getTitre());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande : " + e.getMessage());
        }
    }

    @FXML
    private void addArtwork(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterartwork.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Artwork");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'interface d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void btnupdate() {
        artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

        if (selectedArtwork != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierartwork.fxml"));
                Parent root = loader.load();

                modifierartworkController controller = loader.getController();
                controller.setArtwork(selectedArtwork);
                controller.setParentController(this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Œuvre - " + selectedArtwork.getTitre());
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir l'interface de modification : " + e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une œuvre à modifier.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void deleteart(ActionEvent event) {
        artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

        if (selectedArtwork != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selectedArtwork.getTitre() + "'?");

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                try {
                    service.delete(selectedArtwork);
                    tableView.getItems().remove(selectedArtwork);
                    showAlert("Succès", "L'œuvre a été supprimée avec succès.");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une œuvre à supprimer.");
        }
    }

    public void refreshTable() {
        List<artwork> artworks = service.getAll();
        tableView.setItems(FXCollections.observableArrayList(artworks));
    }
}
