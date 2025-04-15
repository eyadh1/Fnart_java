package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.models.beneficiaires;
import tn.esprit.models.dons;
import tn.esprit.services.ServicesBeneficiaires;
import tn.esprit.services.ServicesDons;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddDonsController implements Initializable {

    @FXML
    private TextField ValeurTextField;

    @FXML
    private ChoiceBox<String> TypeChoice;

    @FXML
    private TextArea DescriptionTextArea;

    @FXML
    private ChoiceBox<beneficiaires> BeneficiaireChoice;

    @FXML
    private Button AjoutButton;

    @FXML
    private Button ListeButton;

    @FXML
    private Button ListeBeneficiairesButton;

    @FXML
    private Button retourButton;

    private final ServicesDons servicesDons = new ServicesDons();
    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();

    // Number validation pattern
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize type choices
        TypeChoice.getItems().addAll("Argent", "Materiel", "Locale", "Oeuvre");
        TypeChoice.setValue("Argent");

        // Add input validation listener for valeur
        ValeurTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidNumber(newValue)) {
                ValeurTextField.setStyle("-fx-border-color: red;");
            } else {
                ValeurTextField.setStyle("");
            }
        });

        // Load beneficiaires into the choice box
        loadBeneficiaires();

        // Set up button actions
        AjoutButton.setOnAction(event -> handleSubmit());
        ListeButton.setOnAction(event -> handleListe());
        ListeBeneficiairesButton.setOnAction(event -> handleListeBeneficiaires());
        retourButton.setOnAction(event -> handleRetour());
    }

    private boolean isValidNumber(String number) {
        return NUMBER_PATTERN.matcher(number).matches();
    }

    private void loadBeneficiaires() {
        try {
            List<beneficiaires> beneficiairesList = servicesBeneficiaires.getAll();
            BeneficiaireChoice.getItems().clear();
            BeneficiaireChoice.getItems().addAll(beneficiairesList);
            
            // Set converter to display beneficiaire name in the choice box
            BeneficiaireChoice.setConverter(new StringConverter<beneficiaires>() {
                @Override
                public String toString(beneficiaires beneficiaire) {
                    return beneficiaire == null ? "" : beneficiaire.getNom();
                }

                @Override
                public beneficiaires fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les bénéficiaires");
            e.printStackTrace();
        }
    }

    private void handleSubmit() {
        try {
            // Validate all required fields
            if (ValeurTextField.getText().trim().isEmpty()) {
                showAlert("Erreur", "Le champ 'Valeur' est obligatoire");
                return;
            }

            if (!isValidNumber(ValeurTextField.getText())) {
                showAlert("Erreur", "La valeur doit être un nombre valide");
                return;
            }

            if (TypeChoice.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner un type de don");
                return;
            }

            if (DescriptionTextArea.getText().trim().isEmpty()) {
                showAlert("Erreur", "Le champ 'Description' est obligatoire");
                return;
            }

            beneficiaires selectedBeneficiaire = BeneficiaireChoice.getValue();
            if (selectedBeneficiaire == null) {
                showAlert("Erreur", "Veuillez sélectionner un bénéficiaire");
                return;
            }

            // Create new don
            dons don = new dons();
            don.setValeur(new BigDecimal(ValeurTextField.getText()));
            don.setType(TypeChoice.getValue());
            don.setDescription(DescriptionTextArea.getText());
            don.setBeneficiaire(selectedBeneficiaire);

            // Add to database
            servicesDons.add(don);

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Le don a été ajouté avec succès !");
            successAlert.showAndWait();

            // Clear fields
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La valeur doit être un nombre valide");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du don");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        ValeurTextField.clear();
        TypeChoice.setValue("Argent");
        DescriptionTextArea.clear();
        BeneficiaireChoice.setValue(null);
    }

    private void handleListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDons.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ListeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleListeBeneficiaires() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeBeneficiaires.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ListeBeneficiairesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Home.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(AddDonsController.class.getResource("/AddDons.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ajouter un Don");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la fenêtre d'ajout de don.");
            alert.showAndWait();
        }
    }
} 