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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListeBeneficiairesBackController implements Initializable {

    @FXML
    private ListView<Beneficiaires> beneficiairesListView;


    @FXML
    private ChoiceBox<String> sortChoice;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();
    private ObservableList<Beneficiaires> beneficiairesList;
    private FilteredList<Beneficiaires> filteredBeneficiaires;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load beneficiaires
        loadBeneficiaires();

        // Set up search functionality
        setupSearch();

        // Set up back button
        backButton.setOnAction(event -> handleBack());

        // Set up add button
        addButton.setOnAction(event -> handleAdd());
    }

    private void loadBeneficiaires() {
        try {
            beneficiairesList = FXCollections.observableArrayList(servicesBeneficiaires.getAll());
            filteredBeneficiaires = new FilteredList<>(beneficiairesList, p -> true);

            beneficiairesListView.setCellFactory(lv -> new ListCell<Beneficiaires>() {
                @Override
                protected void updateItem(Beneficiaires item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox mainBox = new HBox(10);
                        mainBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");

                        VBox imageContainer = new VBox();
                        imageContainer.setPrefWidth(100);
                        imageContainer.setMinWidth(100);

                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);
                        imageView.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

                        // FIXED image loading
                        String imagePath = item.getImage();
                        File imageFile = new File("src/main/resources/" + imagePath);
                        if (imagePath != null && !imagePath.equals("default_image.jpg") && imageFile.exists()) {
                            imageView.setImage(new Image(imageFile.toURI().toString()));
                        } else {
                            File placeholder = new File("src/main/resources/images/placeholder.png");
                            if (placeholder.exists()) {
                                imageView.setImage(new Image(placeholder.toURI().toString()));
                            } else {
                                System.err.println("Placeholder image not found!");
                            }
                        }

                        imageContainer.getChildren().add(imageView);

                        VBox contentBox = new VBox(5);
                        HBox headerBox = new HBox(10);
                        Label nameLabel = new Label(item.getNom());
                        nameLabel.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold;");

                        Label statusLabel = new Label("Statut: " + (item.getStatus() != null ? item.getStatus() : "En attente"));
                        statusLabel.setStyle("-fx-text-fill: #34495E;");
                        headerBox.getChildren().addAll(nameLabel, statusLabel);

                        HBox contactBox = new HBox(10);
                        Label emailLabel = new Label("Email: " + item.getEmail());
                        Label phoneLabel = new Label("Téléphone: " + item.getTelephone());
                        emailLabel.setStyle("-fx-text-fill: #34495E;");
                        phoneLabel.setStyle("-fx-text-fill: #34495E;");
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
                        Button detailButton = new Button("Détails");
                        detailButton.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold;");
                        detailButton.setOnAction(event -> handleDetail(item));
                        actionBox.getChildren().add(detailButton);

                        Button updateButton = new Button("Modifier");
                        updateButton.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold;");
                        updateButton.setOnAction(event -> handleUpdate(item));
                        actionBox.getChildren().add(updateButton);

                        String status = item.getStatus();
                        if (status == null || status.isEmpty() || status.equals("en attente") || status.equals("Refusé")) {
                            Button acceptButton = new Button("Accepter");
                            acceptButton.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold;");
                            acceptButton.setOnAction(event -> handleAccept(item));
                            actionBox.getChildren().add(acceptButton);
                        }
                        if (status == null || status.isEmpty() || status.equals("en attente") || status.equals("Accepté")) {
                            Button rejectButton = new Button("Refuser");
                            rejectButton.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold;");
                            rejectButton.setOnAction(event -> handleReject(item));
                            actionBox.getChildren().add(rejectButton);
                        }

                        contentBox.getChildren().addAll(headerBox, contactBox, detailsBox, descriptionBox, actionBox);
                        mainBox.getChildren().addAll(imageContainer, contentBox);
                        setGraphic(mainBox);
                    }
                }
            });

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
                       beneficiaire.getCause().toLowerCase().contains(lowerCaseFilter) ||
                       (beneficiaire.getStatus() != null && beneficiaire.getStatus().toLowerCase().contains(lowerCaseFilter));
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
    private void handleAccept(Beneficiaires beneficiaire) {
        try {
            beneficiaire.setStatus("Accepté");
            servicesBeneficiaires.update(beneficiaire);
            loadBeneficiaires(); // Refresh the list
            showSuccessAlert("Succès", "Le bénéficiaire a été accepté avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'accepter le bénéficiaire: " + e.getMessage());
        }
    }

    private void handleReject(Beneficiaires beneficiaire) {
        try {
            beneficiaire.setStatus("Refusé");
            servicesBeneficiaires.update(beneficiaire);
            loadBeneficiaires(); // Refresh the list
            showSuccessAlert("Succès", "Le bénéficiaire a été refusé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de refuser le bénéficiaire: " + e.getMessage());
        }
    }
    
    private void handleDetail(Beneficiaires beneficiaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailBeneficiaireBack.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the beneficiaire object
            DetailBeneficiaireBackController controller = loader.getController();
            controller.setBeneficiaire(beneficiaire);
            
            Stage stage = new Stage();
            stage.setTitle("Détails du bénéficiaire");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails du bénéficiaire: " + e.getMessage());
        }
    }
    
    private void handleUpdate(Beneficiaires beneficiaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateBeneficiaireBack.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the beneficiaire object
            UpdateBeneficiaireBackController controller = loader.getController();
            controller.setBeneficiaire(beneficiaire);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier le bénéficiaire");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh the list when the update form is closed
            stage.setOnHidden(event -> loadBeneficiaires());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            System.out.println("Retour à l'accueil...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à l'accueil: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à l'accueil: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            System.out.println("Ouverture du formulaire d'ajout...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBeneficiaireBack.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter un bénéficiaire");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(ListeBeneficiairesBackController.class.getResource("/ListeBeneficiairesBack.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestion des Bénéficiaires");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la liste des bénéficiaires: " + e.getMessage());
            alert.showAndWait();
        }
    }
} 