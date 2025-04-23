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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.util.Callback;
import javafx.util.StringConverter;
import tn.esprit.models.Beneficiaires;
import tn.esprit.models.Dons;
import tn.esprit.services.ServicesBeneficiaires;
import tn.esprit.services.ServicesDons;

public class AdminDashboardController implements Initializable {
    @FXML
    private AnchorPane beneficiairesPane;
    @FXML
    private AnchorPane donsPane;
    @FXML
    private ListView<Beneficiaires> beneficiairesListView;
    @FXML
    private ListView<Dons> donsListView;
    @FXML
    private TextField searchTextField;
    private FilteredList<Beneficiaires> filteredBeneficiaires;
    private FilteredList<Dons> filteredDons;
    private ObservableList<Dons> donsList;




    // Champs pour le formulaire d'ajout de bénéficiaire
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField telephoneTextField;
    @FXML
    private ChoiceBox<String> associationChoice;
    @FXML
    private TextField causeTextField;
    @FXML
    private TextField valeurTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ChoiceBox<String> statusChoice;
    @FXML
    private Button uploadImageButton;
    @FXML
    private Label imagePathLabel;
    @FXML
    private Button ajoutButton;

    // Champs pour le formulaire d'ajout de don
    @FXML
    private TextArea descriptionDonTextArea;
    @FXML
    private TextField valeurDonTextField;
    @FXML
    private ChoiceBox<String> typeDonChoice;
    @FXML
    private Button uploadDonImageButton;
    @FXML
    private Label donImagePathLabel;
    @FXML
    private Button ajoutDonButton;

    private ServicesBeneficiaires servicesBeneficiaires;
    private ServicesDons servicesDons;
    private ObservableList<Beneficiaires> beneficiairesList;

    @FXML
    private ChoiceBox<String> sortOrderChoice;
    @FXML
    private ChoiceBox<String> typeFilterChoice;
    @FXML
    private ChoiceBox<Beneficiaires> beneficiaireChoice;

    @FXML
    private ImageView imagePreview;
    private String selectedImagePath;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize services
        servicesBeneficiaires = new ServicesBeneficiaires();
        servicesDons = new ServicesDons();
        
        // Initialize lists
        beneficiairesList = FXCollections.observableArrayList();
        donsList = FXCollections.observableArrayList();
        
        // Initialize choice boxes
        statusChoice.setItems(FXCollections.observableArrayList("En attente", "Accepté", "Refusé"));
        statusChoice.setValue("En attente");
        
        associationChoice.setItems(FXCollections.observableArrayList("Oui", "Non"));
        
        typeDonChoice.setItems(FXCollections.observableArrayList("Matériel", "Financier", "Service"));
        
        // Load beneficiaires for the choice box in dons form
        beneficiaireChoice.setItems(FXCollections.observableArrayList(servicesBeneficiaires.getAll()));
        
        // Load data
        loadBeneficiaires();
        loadDons();
        
        // Setup search and filter functionality
        setupBeneficiairesSearchAndFilter();
        setupDonsSearchAndFilter();
    }

    private void setupInputValidation() {
        // Email validation
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!EMAIL_PATTERN.matcher(newValue).matches()) {
                emailTextField.setStyle("-fx-border-color: red;");
            } else {
                emailTextField.setStyle("");
            }
        });

        // Phone validation
        telephoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!PHONE_PATTERN.matcher(newValue).matches()) {
                telephoneTextField.setStyle("-fx-border-color: red;");
            } else {
                telephoneTextField.setStyle("");
            }
        });

        // Numeric validation for valeurTextField
        valeurTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                valeurTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Numeric validation for valeurDonTextField
        valeurDonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                valeurDonTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setupBeneficiairesSearchAndFilter() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue == null ? "" : newValue.toLowerCase();
            List<Beneficiaires> filteredList = beneficiairesList.stream()
                    .filter(beneficiaire -> beneficiaire.getNom().toLowerCase().contains(lowerCaseFilter) ||
                            beneficiaire.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                            beneficiaire.getCause().toLowerCase().contains(lowerCaseFilter))
                    .collect(Collectors.toList()); // Collecte les éléments filtrés

            beneficiairesListView.setItems(FXCollections.observableArrayList(filteredList));
        });
        sortOrderChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            Comparator<Beneficiaires> comparator = (b1, b2) -> {
                if ("A-Z".equals(newValue)) {
                    return b1.getNom().compareToIgnoreCase(b2.getNom());
                } else {
                    return b2.getNom().compareToIgnoreCase(b1.getNom());
                }
            };

            // Tri avec Stream
            List<Beneficiaires> sortedList = beneficiairesList.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList()); // Tri et collecte dans une liste

            beneficiairesListView.setItems(FXCollections.observableArrayList(sortedList));
        });
    }
    private void setupDonsSearchAndFilter() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue == null ? "" : newValue.toLowerCase();
            List<Dons> filteredList = donsList.stream()
                    .filter(don -> don.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            (don.getBeneficiaire() != null && don.getBeneficiaire().getNom().toLowerCase().contains(lowerCaseFilter)))
                    .collect(Collectors.toList());

            donsListView.setItems(FXCollections.observableArrayList(filteredList));
        });

        // Filtrage par type avec Stream
        typeFilterChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            List<Dons> filteredByTypeList = donsList.stream()
                    .filter(don -> "Tous".equals(newValue) || don.getType().equals(newValue))
                    .collect(Collectors.toList());

            donsListView.setItems(FXCollections.observableArrayList(filteredByTypeList));
        });
    }

    @FXML
    private void handleAccept(Beneficiaires beneficiaire) {
        try {
            beneficiaire.setStatus("Accepté");
            servicesBeneficiaires.update(beneficiaire);
            loadBeneficiaires();
            showSuccessAlert("Succès", "Le bénéficiaire a été accepté avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'accepter le bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleReject(Beneficiaires beneficiaire) {
        try {
            beneficiaire.setStatus("Refusé");
            servicesBeneficiaires.update(beneficiaire);
            loadBeneficiaires();
            showSuccessAlert("Succès", "Le bénéficiaire a été refusé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de refuser le bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(Beneficiaires beneficiaire) {
        try {
            servicesBeneficiaires.delete(beneficiaire);
            loadBeneficiaires();
            showSuccessAlert("Succès", "Le bénéficiaire a été supprimé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
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



    @FXML
    public void handleUpdate(Beneficiaires beneficiaire) {
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
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleSubmit() {
        if (!validateBeneficiaireForm()) {
            return;
        }

        try {
            Beneficiaires beneficiaire = new Beneficiaires();
            beneficiaire.setNom(nomTextField.getText());
            beneficiaire.setEmail(emailTextField.getText());
            beneficiaire.setTelephone(telephoneTextField.getText());
            beneficiaire.setEstElleAssociation(associationChoice.getValue());
            beneficiaire.setCause(causeTextField.getText());
            beneficiaire.setValeurDemande(Double.parseDouble(valeurTextField.getText()));
            beneficiaire.setDescription(descriptionTextArea.getText());
            beneficiaire.setStatus(statusChoice.getValue());
            beneficiaire.setImage(selectedImagePath);

            servicesBeneficiaires.add(beneficiaire);
            loadBeneficiaires();

            clearBeneficiaireForm();
            selectedImagePath = null;
            imagePreview.setImage(null);

            showSuccessAlert("Succès", "Bénéficiaire ajouté avec succès");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleDonSubmit() {
        if (!validateDonForm()) {
            return;
        }

        try {
            Dons don = new Dons();
            don.setDescription(descriptionDonTextArea.getText());
            don.setValeur(Double.parseDouble(valeurDonTextField.getText()));
            don.setType(typeDonChoice.getValue());
            don.setBeneficiaire(beneficiaireChoice.getValue());

            servicesDons.add(don);
            loadDons();

            clearDonForm();
            showSuccessAlert("Succès", "Don ajouté avec succès");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du don: " + e.getMessage());
        }
    }

    private boolean validateBeneficiaireForm() {
        if (nomTextField.getText().isEmpty() ||
            emailTextField.getText().isEmpty() ||
            telephoneTextField.getText().isEmpty() ||
            associationChoice.getValue() == null ||
            causeTextField.getText().isEmpty() ||
            valeurTextField.getText().isEmpty() ||
            descriptionTextArea.getText().isEmpty() ||
            selectedImagePath == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(emailTextField.getText()).matches()) {
            showAlert("Erreur", "Format d'email invalide");
            return false;
        }

        if (!PHONE_PATTERN.matcher(telephoneTextField.getText()).matches()) {
            showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres");
            return false;
        }

        return true;
    }

    private boolean validateDonForm() {
        if (descriptionDonTextArea.getText().isEmpty() ||
            valeurDonTextField.getText().isEmpty() ||
            typeDonChoice.getValue() == null ||
            beneficiaireChoice.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        return true;
    }

    private void clearBeneficiaireForm() {
        nomTextField.clear();
        emailTextField.clear();
        telephoneTextField.clear();
        associationChoice.setValue(null);
        causeTextField.clear();
        valeurTextField.clear();
        descriptionTextArea.clear();
        statusChoice.setValue("En attente");
        imagePreview.setImage(null);
    }

    private void clearDonForm() {
        descriptionDonTextArea.clear();
        valeurDonTextField.clear();
        typeDonChoice.setValue(null);
        beneficiaireChoice.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Method to make the scene responsive
    private void makeSceneResponsive(Scene scene, Stage stage) {
        // Get screen dimensions
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Set initial size
        stage.setWidth(Math.min(900, screenWidth * 0.9));
        stage.setHeight(Math.min(700, screenHeight * 0.9));

        // Center the stage
        stage.centerOnScreen();
    }

    @FXML
    private void handleFilterEnAttente() {
        filteredBeneficiaires.setPredicate(beneficiaire -> 
            beneficiaire.getStatus() != null && 
            beneficiaire.getStatus().equalsIgnoreCase("en attente")
        );
    }

    @FXML
    private void handleFilterAccepte() {
        filteredBeneficiaires.setPredicate(beneficiaire -> 
            beneficiaire.getStatus() != null && 
            beneficiaire.getStatus().equalsIgnoreCase("accepté")
        );
    }

    @FXML
    private void handleFilterRefuse() {
        filteredBeneficiaires.setPredicate(beneficiaire -> 
            beneficiaire.getStatus() != null && 
            beneficiaire.getStatus().equalsIgnoreCase("refusé")
        );
    }

    private void loadBeneficiaires() {
        try {
            beneficiairesList.setAll(servicesBeneficiaires.getAll());
            filteredBeneficiaires = new FilteredList<>(beneficiairesList, p -> true);

            beneficiairesListView.setCellFactory(lv -> new ListCell<Beneficiaires>() {
                private final Button modifierBtn = new Button("Modifier");
                private final Button detailsBtn = new Button("Détails");
                private final Button accepterBtn = new Button("Accepter");
                private final Button refuserBtn = new Button("Refuser");

                {
                    modifierBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    detailsBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    accepterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    refuserBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                }

                @Override
                protected void updateItem(Beneficiaires item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox card = new VBox(10);
                        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

                        // Image container
                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);

                        // Load image
                        if (item.getImage() != null && !item.getImage().isEmpty()) {
                            try {
                                File imageFile = new File(item.getImage());
                                if (imageFile.exists()) {
                                    Image image = new Image(imageFile.toURI().toString());
                                    imageView.setImage(image);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Info container
                        VBox infoBox = new VBox(5);
                        Label nomLabel = new Label("Nom: " + item.getNom());
                        Label emailLabel = new Label("Email: " + item.getEmail());
                        Label telLabel = new Label("Tél: " + item.getTelephone());
                        Label causeLabel = new Label("Cause: " + item.getCause());
                        Label statusLabel = new Label("Statut: " + (item.getStatus() != null ? item.getStatus() : "En attente"));

                        infoBox.getChildren().addAll(nomLabel, emailLabel, telLabel, causeLabel, statusLabel);

                        // Buttons container
                        HBox buttonsBox = new HBox(10);
                        modifierBtn.setOnAction(e -> handleUpdate(item));
                        detailsBtn.setOnAction(e -> handleDetail(item));
                        accepterBtn.setOnAction(e -> handleAccept(item));
                        refuserBtn.setOnAction(e -> handleReject(item));

                        buttonsBox.getChildren().addAll(modifierBtn, detailsBtn, accepterBtn, refuserBtn);

                        // Add all components to the card
                        card.getChildren().addAll(imageView, infoBox, buttonsBox);
                        setGraphic(card);
                    }
                }
            });

            beneficiairesListView.setItems(filteredBeneficiaires);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les bénéficiaires");
            e.printStackTrace();
        }
    }


    private void loadDons() {
        try {
            donsList.setAll(servicesDons.getAll());
            filteredDons = new FilteredList<>(donsList, p -> true);

            // Set up the list view cell factory
            donsListView.setCellFactory(new Callback<ListView<Dons>, ListCell<Dons>>() {
                @Override
                public ListCell<Dons> call(ListView<Dons> param) {
                    return new ListCell<Dons>() {
                        @Override
                        protected void updateItem(Dons item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                VBox vbox = new VBox(5);


                                HBox headerBox = new HBox(10);
                                Label typeLabel = new Label("Type: " + item.getType());

                                Label valeurLabel = new Label("Valeur: " + item.getValeur());

                                headerBox.getChildren().addAll(typeLabel, valeurLabel);

                                Label descriptionLabel = new Label("Description: " + item.getDescription());
                                descriptionLabel.setWrapText(true);

                                HBox beneficiaireBox = new HBox(10);
                                Label beneficiaireLabel = new Label("Bénéficiaire: " +
                                        (item.getBeneficiaire() != null ? item.getBeneficiaire().getNom() : "Non spécifié"));

                                beneficiaireBox.getChildren().add(beneficiaireLabel);

                                // Add detail button
                                Button detailButton = new Button("Détails");
                                detailButton.setOnAction(event -> handleDetailDon(item));

                                HBox buttonBox = new HBox(10);
                                buttonBox.getChildren().add(detailButton);

                                vbox.getChildren().addAll(headerBox, descriptionLabel, beneficiaireBox, buttonBox);
                                setGraphic(vbox);
                            }
                        }
                    };
                }
            });

            // Set the items to the filtered list
            donsListView.setItems(filteredDons);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les dons");
            e.printStackTrace();
        }
    }

    private void handleDetailDon(Dons don) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailDonsBack.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the don object
            DetailDonsBackController controller = (DetailDonsBackController) loader.getController();
            controller.setdons(don);

            Stage stage = new Stage();
            stage.setTitle("Détails du don");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails du don: " + e.getMessage());
        }

    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
            imagePreview.setFitWidth(100);
            imagePreview.setFitHeight(100);
            imagePreview.setPreserveRatio(true);
        }
    }

}