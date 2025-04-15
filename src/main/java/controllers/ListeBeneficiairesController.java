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
import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeBeneficiairesController implements Initializable {

    @FXML
    private ListView<beneficiaires> beneficiairesListView;

    @FXML
    private Button backButton;

    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the background color of the list view
        beneficiairesListView.setStyle("-fx-background-color: #EFDAC7;");
        
        // Style the back button
        backButton.setStyle("-fx-background-color: #A6695B; -fx-text-fill: #F2F2F2; -fx-font-weight: bold;");
        
        loadBeneficiaires();
        backButton.setOnAction(event -> handleBack());
        
        // Add double-click handler to open update form
        beneficiairesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                beneficiaires selected = beneficiairesListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openUpdateForm(selected);
                }
            }
        });
    }

    private void loadBeneficiaires() {
        List<beneficiaires> beneficiairesList = servicesBeneficiaires.getAll();
        beneficiairesListView.getItems().setAll(beneficiairesList);
        
        // Set cell factory to display beneficiaires information in a custom format
        beneficiairesListView.setCellFactory(lv -> new javafx.scene.control.ListCell<beneficiaires>() {
            @Override
            protected void updateItem(beneficiaires item, boolean empty) {
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
                    Label nameLabel = new Label("Nom: " + item.getNom());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #F2F2F2;");

                    HBox contactInfo = new HBox(20);
                    Label emailLabel = new Label("Email: " + item.getEmail());
                    Label phoneLabel = new Label("Téléphone: " + item.getTelephone());
                    emailLabel.setStyle("-fx-text-fill: #F2F2F2;");
                    phoneLabel.setStyle("-fx-text-fill: #F2F2F2;");
                    contactInfo.getChildren().addAll(emailLabel, phoneLabel);

                    HBox details = new HBox(20);
                    Label causeLabel = new Label("Cause: " + item.getCause());
                    Label associationLabel = new Label("Association: " + item.getEstElleAssociation());
                    causeLabel.setStyle("-fx-text-fill: #F2F2F2;");
                    associationLabel.setStyle("-fx-text-fill: #F2F2F2;");
                    details.getChildren().addAll(causeLabel, associationLabel);

                    Label descriptionLabel = new Label("Description: " + item.getDescription());
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setMaxWidth(600);
                    descriptionLabel.setStyle("-fx-text-fill: #F2F2F2;");

                    Label valueLabel = new Label("Valeur demandée: " + 
                        (item.getValeurDemande() != null ? item.getValeurDemande().toString() : "Non spécifiée"));
                    valueLabel.setStyle("-fx-text-fill: #F2F2F2;");

                    // Add all labels to the VBox
                    vbox.getChildren().addAll(
                        nameLabel,
                        contactInfo,
                        details,
                        descriptionLabel,
                        valueLabel
                    );

                    setGraphic(vbox);
                    setText(null);
                }
            }
        });
    }

    private void openUpdateForm(beneficiaires beneficiaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateBeneficiaire.fxml"));
            Parent root = loader.load();
            
            UpdateBeneficiaireController controller = loader.getController();
            controller.setBeneficiaire(beneficiaire);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un bénéficiaire");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh the list when the update form is closed
            stage.setOnHidden(event -> loadBeneficiaires());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBeneficiaire.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
