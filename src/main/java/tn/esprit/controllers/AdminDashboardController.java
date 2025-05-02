package tn.esprit.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.chart.PieChart;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.util.Callback;
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
    private Label Description;

    @FXML
    private Label valeur;
    @FXML
    private Label type;
    @FXML
    private Label beneficiaire;

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
    private ChoiceBox<String> beneficiaireChoice;
    @FXML
    private Button ajoutDonButton;
    @FXML
    private Label nomDonErrorLabel;

    private ServicesBeneficiaires servicesBeneficiaires;
    private ServicesDons servicesDons;
    private ObservableList<Beneficiaires> beneficiairesList;

    @FXML
    private ChoiceBox<String> sortOrderChoice;
    @FXML
    private ChoiceBox<String> typeFilterChoice;

    @FXML
    private ImageView imagePreview;
    private String selectedImagePath;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");

    // Error labels
    @FXML private Label nomErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label telephoneErrorLabel;
    @FXML private Label associationErrorLabel;
    @FXML private Label causeErrorLabel;
    @FXML private Label valeurErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label statusErrorLabel;
    @FXML private Label imageErrorLabel;
    @FXML private Label descriptionDonErrorLabel;
    @FXML private Label valeurDonErrorLabel;
    @FXML private Label typeDonErrorLabel;
    @FXML private Label beneficiaireErrorLabel;

    // Statistics labels
    @FXML private Label totalBeneficiairesLabel;
    @FXML private Label enAttenteLabel;
    @FXML private Label acceptesLabel;
    @FXML private Label refusesLabel;
    @FXML private Label totalDonsLabel;
    @FXML private Label valeurTotaleLabel;
    @FXML private Label donsNatureLabel;
    @FXML private Label donsFinanciersLabel;

    // Charts
    @FXML private PieChart beneficiairesChart;
    @FXML private PieChart donsChart;
    @FXML private PieChart beneficiairesStatsChart;
    @FXML private PieChart donsStatsChart;

    @FXML private ToggleGroup associationToggleGroup;
    @FXML private RadioButton associationOui;
    @FXML private RadioButton associationNon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize services
        servicesBeneficiaires = new ServicesBeneficiaires();
        servicesDons = new ServicesDons();

        // Initialize lists
        beneficiairesList = FXCollections.observableArrayList();
        donsList = FXCollections.observableArrayList();

        // Initialize choice boxes
        statusChoice.setItems(FXCollections.observableArrayList("en attente", "accepté", "refusé"));
        statusChoice.setValue("en attente");

        typeDonChoice.setItems(FXCollections.observableArrayList("Argent", "Materiels", "Locale", "Oeuvre"));

        // Initialize sort and filter options
        sortOrderChoice.setItems(FXCollections.observableArrayList("A-Z", "Z-A"));
        typeFilterChoice.setItems(FXCollections.observableArrayList("Tous", "Argent", "Materiels", "Locale", "Oeuvre"));
        typeFilterChoice.setValue("Tous");

        // Load beneficiaires for the choice box in dons form
        ObservableList<String> beneficiaireNames = FXCollections.observableArrayList();
        for (Beneficiaires beneficiaire : servicesBeneficiaires.getAll()) {
            beneficiaireNames.add(beneficiaire.getNom());
        }
        beneficiaireChoice.setItems(beneficiaireNames);

        loadBeneficiaires();
        loadDons();

        setupBeneficiairesSearchAndFilter();
        setupDonsSearchAndFilter();

        setupValidation();
        updateStatistics();

        // Set default value for association radio buttons
        associationNon.setSelected(true);

        // Add validation for association radio buttons
        associationToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                associationErrorLabel.setText("Veuillez sélectionner une option");
                associationOui.getStyleClass().add("error");
                associationNon.getStyleClass().add("error");
            } else {
                associationErrorLabel.setText("");
                associationOui.getStyleClass().remove("error");
                associationNon.getStyleClass().remove("error");
            }
        });
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
        // Initialize filtered list
        filteredBeneficiaires = new FilteredList<>(beneficiairesList, p -> true);

        // Search functionality
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue == null ? "" : newValue.toLowerCase();
            filteredBeneficiaires.setPredicate(beneficiaire ->
                    beneficiaire.getNom().toLowerCase().contains(lowerCaseFilter) ||
                            beneficiaire.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                            beneficiaire.getCause().toLowerCase().contains(lowerCaseFilter)
            );
        });

        // Sort functionality
        sortOrderChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Comparator<Beneficiaires> comparator = (b1, b2) -> {
                    if ("A-Z".equals(newValue)) {
                        return b1.getNom().compareToIgnoreCase(b2.getNom());
                    } else {
                        return b2.getNom().compareToIgnoreCase(b1.getNom());
                    }
                };
                beneficiairesList.sort(comparator);
            }
        });
    }

    private void setupDonsSearchAndFilter() {
        // Initialize filtered list
        filteredDons = new FilteredList<>(donsList, p -> true);

        // Search functionality
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue == null ? "" : newValue.toLowerCase();
            filteredDons.setPredicate(don ->
                    don.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            (don.getBeneficiaire() != null && don.getBeneficiaire().getNom().toLowerCase().contains(lowerCaseFilter))
            );
        });

        // Type filter functionality
        typeFilterChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filteredDons.setPredicate(don ->
                        "Tous".equals(newValue) || don.getType().equals(newValue)
                );
            }
        });
    }

    @FXML
    private void handleAccept(Beneficiaires beneficiaire) {
        try {
            if (beneficiaire != null) {
                beneficiaire.setStatus("accepté");
                servicesBeneficiaires.update(beneficiaire);
                loadBeneficiaires();
                updateStatistics();
                showSuccessAlert("Succès", "Le bénéficiaire a été accepté avec succès.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'accepter le bénéficiaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleReject(Beneficiaires beneficiaire) {
        try {
            if (beneficiaire != null) {
                beneficiaire.setStatus("refusé");
                servicesBeneficiaires.update(beneficiaire);
                loadBeneficiaires();
                updateStatistics();
                showSuccessAlert("Succès", "Le bénéficiaire a été refusé avec succès.");
            }
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
        if (isFormValid()) {
            try {
                Beneficiaires beneficiaire = new Beneficiaires();
                beneficiaire.setNom(nomTextField.getText());
                beneficiaire.setEmail(emailTextField.getText());
                beneficiaire.setTelephone(telephoneTextField.getText());
                beneficiaire.setEstElleAssociation(associationOui.isSelected() ? "Oui" : "Non");
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
    }

    @FXML
    private void handleDonSubmit() {
        if (!setupValidation()) {
            return;
        }

        try {
            Dons don = new Dons();
            don.setDescription(descriptionDonTextArea.getText());

            // Parse and validate valeur
            double valeur = Double.parseDouble(valeurDonTextField.getText());
            if (valeur > 1000000) {
                valeur = 1000000; // Limiter à 1 million
            }
            don.setValeur(valeur);

            don.setType(typeDonChoice.getValue());

            // Find the beneficiaire by name
            String selectedBeneficiaireName = String.valueOf(beneficiaireChoice.getValue());
            Beneficiaires selectedBeneficiaire = servicesBeneficiaires.getAll().stream()
                    .filter(b -> b.getNom().equals(selectedBeneficiaireName))
                    .findFirst()
                    .orElse(null);

            don.setBeneficiaire(selectedBeneficiaire);

            servicesDons.add(don);
            loadDons();

            clearDonForm();
            showSuccessAlert("Succès", "Don ajouté avec succès");
        } catch (Exception e) {
            if (e.getMessage().contains("Data truncation")) {
                showAlert("Erreur", "La valeur est trop grande. Veuillez entrer une valeur plus petite.");
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout du don: " + e.getMessage());
            }
        }
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (nomTextField.getText().isEmpty() ||
                emailTextField.getText().isEmpty() ||
                telephoneTextField.getText().isEmpty() ||
                causeTextField.getText().isEmpty() ||
                valeurTextField.getText().isEmpty() ||
                descriptionTextArea.getText().isEmpty() ||
                selectedImagePath == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
            isValid = false;
        }

        if (!EMAIL_PATTERN.matcher(emailTextField.getText()).matches()) {
            showAlert("Erreur", "Format d'email invalide");
            isValid = false;
        }

        if (!PHONE_PATTERN.matcher(telephoneTextField.getText()).matches()) {
            showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres");
            isValid = false;
        }

        // Validate association selection
        if (associationToggleGroup.getSelectedToggle() == null) {
            associationErrorLabel.setText("Veuillez sélectionner une option");
            associationOui.getStyleClass().add("error");
            associationNon.getStyleClass().add("error");
            isValid = false;
        } else {
            associationErrorLabel.setText("");
            associationOui.getStyleClass().remove("error");
            associationNon.getStyleClass().remove("error");
        }

        return isValid;
    }

    private void clearBeneficiaireForm() {
        nomTextField.clear();
        emailTextField.clear();
        telephoneTextField.clear();
        causeTextField.clear();
        valeurTextField.clear();
        descriptionTextArea.clear();
        statusChoice.setValue("en attente");
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
                        beneficiaire.getStatus().equalsIgnoreCase("En attente")
        );
    }

    @FXML
    private void handleFilterAccepte() {
        filteredBeneficiaires.setPredicate(beneficiaire ->
                beneficiaire.getStatus() != null &&
                        beneficiaire.getStatus().equalsIgnoreCase("Accepté")
        );
    }

    @FXML
    private void handleFilterRefuse() {
        filteredBeneficiaires.setPredicate(beneficiaire ->
                beneficiaire.getStatus() != null &&
                        beneficiaire.getStatus().equalsIgnoreCase("Refusé")
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
                        Label statusLabel = new Label("Statut: " + (item.getStatus() != null ? item.getStatus() : "en attente"));

                        infoBox.getChildren().addAll(nomLabel, emailLabel, telLabel, causeLabel, statusLabel);

                        // Buttons container
                        HBox buttonsBox = new HBox(10);
                        modifierBtn.setOnAction(e -> handleUpdate(item));
                        detailsBtn.setOnAction(e -> handleDetail(item));
                        accepterBtn.setOnAction(e -> handleAccept(item));
                        refuserBtn.setOnAction(e -> handleReject(item));

                        // Show/hide buttons based on status
                        String status = item.getStatus() != null ? item.getStatus() : "en attente";
                        switch (status) {
                            case "en attente":
                                accepterBtn.setVisible(true);
                                refuserBtn.setVisible(true);
                                break;
                            case "Accepté":
                                accepterBtn.setVisible(false);
                                refuserBtn.setVisible(true);
                                break;
                            case "Refusé":
                                accepterBtn.setVisible(true);
                                refuserBtn.setVisible(false);
                                break;
                        }

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

    private boolean setupValidation() {
        // Description validation
        descriptionDonTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                descriptionDonErrorLabel.setText("La description est requise");
                descriptionDonTextArea.setStyle("-fx-border-color: red;");
            } else {
                descriptionDonErrorLabel.setText("");
                descriptionDonTextArea.setStyle("-fx-border-color: green;");
            }
        });

        // Valeur validation
        valeurDonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                valeurDonErrorLabel.setText("La valeur est requise");
                valeurDonTextField.setStyle("-fx-border-color: red;");
            } else if (!NUMBER_PATTERN.matcher(newValue).matches()) {
                valeurDonErrorLabel.setText("Format de valeur invalide");
                valeurDonTextField.setStyle("-fx-border-color: red;");
            } else {
                valeurDonErrorLabel.setText("");
                valeurDonTextField.setStyle("-fx-border-color: green;");
            }
        });

        // Type validation
        typeDonChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                typeDonErrorLabel.setText("Le type est requis");
                typeDonChoice.setStyle("-fx-border-color: red;");
            } else {
                typeDonErrorLabel.setText("");
                typeDonChoice.setStyle("-fx-border-color: green;");
            }
        });

        // Beneficiaire validation
        beneficiaireChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                beneficiaireErrorLabel.setText("Le bénéficiaire est requis");
                beneficiaireChoice.setStyle("-fx-border-color: red;");
            } else {
                beneficiaireErrorLabel.setText("");
                beneficiaireChoice.setStyle("-fx-border-color: green;");
            }
        });

        return true;
    }

    private void updateStatistics() {
        // Update beneficiaires statistics
        int total = beneficiairesList.size();
        int enAttente = (int) beneficiairesList.stream().filter(b -> "en attente".equals(b.getStatus())).count();
        int acceptes = (int) beneficiairesList.stream().filter(b -> "accepté".equals(b.getStatus())).count();
        int refuses = (int) beneficiairesList.stream().filter(b -> "refusé".equals(b.getStatus())).count();

        totalBeneficiairesLabel.setText(String.valueOf(total));
        enAttenteLabel.setText(String.valueOf(enAttente));
        acceptesLabel.setText(String.valueOf(acceptes));
        refusesLabel.setText(String.valueOf(refuses));

        // Update dons statistics
        int totalDons = donsList.size();
        double valeurTotale = donsList.stream().mapToDouble(Dons::getValeur).sum();
        int donsNature = (int) donsList.stream().filter(d -> "Materiels".equals(d.getType())).count();
        int donsFinanciers = (int) donsList.stream().filter(d -> "Argent".equals(d.getType())).count();

        totalDonsLabel.setText(String.valueOf(totalDons));
        valeurTotaleLabel.setText(String.format("%.2f DT", valeurTotale));
        donsNatureLabel.setText(String.valueOf(donsNature));
        donsFinanciersLabel.setText(String.valueOf(donsFinanciers));

        // Update charts
        updateCharts();
    }

    private void updateCharts() {
        // Clear existing data
        beneficiairesChart.getData().clear();
        beneficiairesStatsChart.getData().clear();
        donsChart.getData().clear();
        donsStatsChart.getData().clear();

        // Update beneficiaires charts
        ObservableList<PieChart.Data> beneficiairesData = FXCollections.observableArrayList(
                new PieChart.Data("En Attente",
                        beneficiairesList.stream().filter(b -> "en attente".equals(b.getStatus())).count()),
                new PieChart.Data("Acceptés",
                        beneficiairesList.stream().filter(b -> "accepté".equals(b.getStatus())).count()),
                new PieChart.Data("Refusés",
                        beneficiairesList.stream().filter(b -> "refusé".equals(b.getStatus())).count())
        );
        beneficiairesChart.setData(beneficiairesData);
        beneficiairesStatsChart.setData(FXCollections.observableArrayList(beneficiairesData));

        // Update dons charts
        ObservableList<PieChart.Data> donsData = FXCollections.observableArrayList(
                new PieChart.Data("Dons en Nature",
                        donsList.stream().filter(d -> "Materiels".equals(d.getType())).count()),
                new PieChart.Data("Dons Financiers",
                        donsList.stream().filter(d -> "Argent".equals(d.getType())).count())
        );
        donsChart.setData(donsData);
        donsStatsChart.setData(FXCollections.observableArrayList(donsData));
    }

    @FXML
    private void handleExportBeneficiairePDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Beneficiaires.pdf"));
            document.open();
            document.add(new Paragraph("Liste des Bénéficiaires"));
            // Add logic to fetch and add beneficiary data to the PDF
            document.close();
            System.out.println("Beneficiary list exported to PDF.");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportDonsPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Dons.pdf"));
            document.open();
            document.add(new Paragraph("Liste des Dons"));
            // Add logic to fetch and add donation data to the PDF
            document.close();
            System.out.println("Donations list exported to PDF.");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}