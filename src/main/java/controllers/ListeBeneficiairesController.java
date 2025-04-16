package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListeBeneficiairesController implements Initializable {

    @FXML
    private ListView<beneficiaires> beneficiairesListView;

    @FXML
    private TextField searchTextField;

    @FXML
    private ChoiceBox<String> sortChoice;

    @FXML
    private Button backButton;

    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();
    private ObservableList<beneficiaires> beneficiairesList;
    private FilteredList<beneficiaires> filteredBeneficiaires;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize sort choices
        sortChoice.getItems().addAll("Nom (A-Z)", "Nom (Z-A)");
        sortChoice.setValue("Nom (A-Z)");

        // Load beneficiaires
        loadBeneficiaires();

        // Set up search functionality
        setupSearch();

        // Set up sorting
        setupSorting();

        // Set up back button
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
        try {
            beneficiairesList = FXCollections.observableArrayList(servicesBeneficiaires.getAll());
            filteredBeneficiaires = new FilteredList<>(beneficiairesList, p -> true);

            // Set up the list view cell factory
            beneficiairesListView.setCellFactory(new Callback<ListView<beneficiaires>, ListCell<beneficiaires>>() {
                @Override
                public ListCell<beneficiaires> call(ListView<beneficiaires> param) {
                    return new ListCell<beneficiaires>() {
                        @Override
                        protected void updateItem(beneficiaires item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                VBox vbox = new VBox(5);
                                vbox.setStyle("-fx-background-color: #A6695B; -fx-padding: 10; -fx-background-radius: 5;");

                                Label nameLabel = new Label(item.getNom());
                                nameLabel.setStyle("-fx-text-fill: #BA9D1F; -fx-font-weight: bold;");

                                HBox contactBox = new HBox(10);
                                Label emailLabel = new Label("Email: " + item.getEmail());
                                Label phoneLabel = new Label("Téléphone: " + item.getTelephone());
                                emailLabel.setStyle("-fx-text-fill: #F2F2F2;");
                                phoneLabel.setStyle("-fx-text-fill: #F2F2F2;");
                                contactBox.getChildren().addAll(emailLabel, phoneLabel);

                                HBox detailsBox = new HBox(10);
                                Label causeLabel = new Label("Cause: " + item.getCause());
                                Label associationLabel = new Label("Association: " + item.getEstElleAssociation());
                                causeLabel.setStyle("-fx-text-fill: #F2F2F2;");
                                associationLabel.setStyle("-fx-text-fill: #F2F2F2;");
                                detailsBox.getChildren().addAll(causeLabel, associationLabel);

                                Label descriptionLabel = new Label(item.getDescription());
                                descriptionLabel.setStyle("-fx-text-fill: #F2F2F2;");
                                descriptionLabel.setWrapText(true);

                                vbox.getChildren().addAll(nameLabel, contactBox, detailsBox, descriptionLabel);
                                setGraphic(vbox);
                            }
                        }
                    };
                }
            });

            // Set the items to the filtered list
            beneficiairesListView.setItems(filteredBeneficiaires);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les bénéficiaires");
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBeneficiaires.setPredicate(beneficiaire -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return beneficiaire.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       beneficiaire.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                       beneficiaire.getTelephone().toLowerCase().contains(lowerCaseFilter) ||
                       beneficiaire.getCause().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupSorting() {
        sortChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            SortedList<beneficiaires> sortedList = new SortedList<>(filteredBeneficiaires);
            if ("Nom (A-Z)".equals(newValue)) {
                sortedList.setComparator((b1, b2) -> b1.getNom().compareToIgnoreCase(b2.getNom()));
            } else if ("Nom (Z-A)".equals(newValue)) {
                sortedList.setComparator((b1, b2) -> b2.getNom().compareToIgnoreCase(b1.getNom()));
            }
            beneficiairesListView.setItems(sortedList);
        });
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(ListeBeneficiairesController.class.getResource("/ListeBeneficiaires.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Liste des Bénéficiaires");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la liste des bénéficiaires.");
            alert.showAndWait();
        }
    }
}
