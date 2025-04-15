package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import tn.esprit.models.dons;
import tn.esprit.services.ServicesDons;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeDonsController implements Initializable {

    @FXML
    private ListView<dons> donsListView;

    @FXML
    private Button backButton;

    private final ServicesDons servicesDons = new ServicesDons();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the background color of the list view
        donsListView.setStyle("-fx-background-color: #EFDAC7;");
        
        // Style the back button
        backButton.setStyle("-fx-background-color: #A6695B; -fx-text-fill: #F2F2F2; -fx-font-weight: bold;");
        
        loadDons();
        backButton.setOnAction(event -> handleBack());
    }

    private void loadDons() {
        try {
            List<dons> donsList = servicesDons.getAll();
            donsListView.getItems().setAll(donsList);
            
            // Set cell factory to display dons information in a custom format
            donsListView.setCellFactory(lv -> new javafx.scene.control.ListCell<dons>() {
                @Override
                protected void updateItem(dons item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        // Create a VBox to hold all the information
                        VBox vbox = new VBox(5);
                        vbox.setPadding(new Insets(10));
                        vbox.setStyle("-fx-background-color: #A6695B; -fx-border-color: #BA9D1F; -fx-border-width: 2; -fx-border-radius: 5;");

                        // Create labels for each piece of information
                        Label valeurLabel = new Label("Valeur: " + item.getValeur() + " TND");
                        valeurLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #F2F2F2;");

                        HBox details = new HBox(20);
                        Label typeLabel = new Label("Type: " + item.getType());
                        typeLabel.setStyle("-fx-text-fill: #F2F2F2;");
                        details.getChildren().add(typeLabel);

                        Label descriptionLabel = new Label("Description: " + item.getDescription());
                        descriptionLabel.setWrapText(true);
                        descriptionLabel.setMaxWidth(600);
                        descriptionLabel.setStyle("-fx-text-fill: #F2F2F2;");

                        // Create a VBox for beneficiaire information
                        VBox beneficiaireInfo = new VBox(5);
                        beneficiaireInfo.setStyle("-fx-background-color: #BA9D1F; -fx-padding: 5; -fx-border-radius: 3;");
                        
                        Label beneficiaireTitle = new Label("Bénéficiaire:");
                        beneficiaireTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #F2F2F2;");
                        
                        Label beneficiaireName = new Label("Nom: " + item.getBeneficiaire().getNom());
                        Label beneficiaireContact = new Label("Contact: " + item.getBeneficiaire().getEmail() + " | " + item.getBeneficiaire().getTelephone());
                        beneficiaireName.setStyle("-fx-text-fill: #F2F2F2;");
                        beneficiaireContact.setStyle("-fx-text-fill: #F2F2F2;");
                        
                        beneficiaireInfo.getChildren().addAll(beneficiaireTitle, beneficiaireName, beneficiaireContact);

                        // Add all labels to the VBox
                        vbox.getChildren().addAll(
                            valeurLabel,
                            details,
                            descriptionLabel,
                            beneficiaireInfo
                        );

                        setGraphic(vbox);
                        setText(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDons.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 