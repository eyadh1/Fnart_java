package tn.esprit.controllers;

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
import tn.esprit.models.Beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class ListeBeneficiairesController implements Initializable {

    @FXML
    private ListView<Beneficiaires> beneficiairesListView;

    @FXML
    private TextField searchTextField;

    @FXML
    private ChoiceBox<String> sortChoice;

    @FXML
    private Button backButton;

    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();
    private ObservableList<Beneficiaires> beneficiairesList;
    private FilteredList<Beneficiaires> filteredBeneficiaires;

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
                Beneficiaires selected = beneficiairesListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openUpdateForm(selected);
                }
            }
        });
    }

    private void loadBeneficiaires() {
        try {
            System.out.println("Chargement des bénéficiaires...");
            List<Beneficiaires> beneficiaires = servicesBeneficiaires.getAll();
            System.out.println("Nombre de bénéficiaires trouvés: " + beneficiaires.size());
            
            beneficiairesList = FXCollections.observableArrayList(beneficiaires);
            filteredBeneficiaires = new FilteredList<>(beneficiairesList, p -> true);

            // Set up the list view cell factory
            beneficiairesListView.setCellFactory(new Callback<ListView<Beneficiaires>, ListCell<Beneficiaires>>() {
                @Override
                public ListCell<Beneficiaires> call(ListView<Beneficiaires> param) {
                    return new ListCell<Beneficiaires>() {
                        @Override
                        protected void updateItem(Beneficiaires item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                HBox mainBox = new HBox(10);
                                mainBox.setStyle("-fx-background-color: #A6695B; -fx-padding: 10; -fx-background-radius: 5;");
                                
                                // Image container
                                VBox imageContainer = new VBox();
                                imageContainer.setPrefWidth(100);
                                imageContainer.setMinWidth(100);
                                
                                // Create image view
                                ImageView imageView = new ImageView();
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                imageView.setPreserveRatio(true);
                                imageView.setStyle("-fx-background-color: #34495E; -fx-background-radius: 5;");
                                
                                // Load image
                                String imagePath = item.getImage();
                                try {
                                    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("default_image.jpg")) {
                                        // Try to load from resources first
                                        try {
                                            Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                                            imageView.setImage(image);
                                        } catch (Exception e) {
                                            // If not found in resources, try as a file
                                            try {
                                                File imageFile = new File(imagePath);
                                                if (imageFile.exists()) {
                                                    imageView.setImage(new Image("file:" + imagePath));
                                                } else {
                                                    // Use a placeholder image
                                                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                                                }
                                            } catch (Exception ex) {
                                                // Use a placeholder image if file not found
                                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                                            }
                                        }
                                    } else {
                                        // Use a placeholder image for default or null image paths
                                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                                    }
                                } catch (Exception e) {
                                    // If all else fails, just show a colored background
                                    imageView.setStyle("-fx-background-color: #34495E; -fx-background-radius: 5;");
                                }
                                
                                imageContainer.getChildren().add(imageView);
                                
                                // Content container
                                VBox contentBox = new VBox(5);
                                
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
                                Label valeurLabel = new Label("Valeur Demandée: " + item.getValeurDemande() + " DT");
                                causeLabel.setStyle("-fx-text-fill: #34495E;");
                                associationLabel.setStyle("-fx-text-fill: #34495E;");
                                valeurLabel.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold;");
                                detailsBox.getChildren().addAll(causeLabel, associationLabel, valeurLabel);

                                VBox descriptionBox = new VBox(5);
                                Label descriptionTitle = new Label("Description:");
                                descriptionTitle.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold;");
                                Label descriptionLabel = new Label(item.getDescription());
                                descriptionLabel.setStyle("-fx-text-fill: #34495E;");
                                descriptionLabel.setWrapText(true);
                                descriptionBox.getChildren().addAll(descriptionTitle, descriptionLabel);

                                HBox actionBox = new HBox(10);
                                Button updateButton = new Button("Modifier");
                                updateButton.setStyle("-fx-background-color: #BA9D1F; -fx-text-fill: #F2F2F2; -fx-padding: 5; -fx-background-radius: 5;");
                                updateButton.setOnAction(event -> openUpdateForm(item));
                                actionBox.getChildren().add(updateButton);

                                contentBox.getChildren().addAll(nameLabel, contactBox, detailsBox, descriptionBox, actionBox);
                                
                                // Add both containers to the main box
                                mainBox.getChildren().addAll(imageContainer, contentBox);
                                
                                setGraphic(mainBox);
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
            SortedList<Beneficiaires> sortedList = new SortedList<>(filteredBeneficiaires);
            if ("Nom (A-Z)".equals(newValue)) {
                sortedList.setComparator((b1, b2) -> b1.getNom().compareToIgnoreCase(b2.getNom()));
            } else if ("Nom (Z-A)".equals(newValue)) {
                sortedList.setComparator((b1, b2) -> b2.getNom().compareToIgnoreCase(b1.getNom()));
            }
            beneficiairesListView.setItems(sortedList);
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openUpdateForm(Beneficiaires beneficiaire) {
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
