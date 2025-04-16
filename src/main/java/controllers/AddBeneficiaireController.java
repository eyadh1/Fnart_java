package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import java.util.regex.Pattern;

public class AddBeneficiaireController implements Initializable {
    @FXML
    private Button retourHome;
    @FXML
    private ChoiceBox<String> AssociationChoice;
    @FXML
    private Button AjoutBoutton;
    @FXML
    private TextField CauseTextField;
    @FXML
    private TextArea DescriptionTextArea;
    @FXML
    private TextField EmailTextField;
    @FXML
    private Button ListeBenebutton;
    @FXML
    private TextField NomTextField;
    @FXML
    private TextField TelephoneTextField;
    @FXML
    private TextField ValeurTextField;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // Telephone validation pattern (8 digits)
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^\\d{8}$");
    // Number validation pattern
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AssociationChoice.getItems().addAll("Oui", "Non");
        AssociationChoice.setValue("Non");

        // Add input validation listeners
        EmailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidEmail(newValue)) {
                EmailTextField.setStyle("-fx-border-color: red;");
            } else {
                EmailTextField.setStyle("");
            }
        });

        TelephoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidTelephone(newValue)) {
                TelephoneTextField.setStyle("-fx-border-color: red;");
            } else {
                TelephoneTextField.setStyle("");
            }
        });

        ValeurTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidNumber(newValue)) {
                ValeurTextField.setStyle("-fx-border-color: red;");
            } else {
                ValeurTextField.setStyle("");
            }
        });

        AjoutBoutton.setOnAction(event -> handleSubmit());
        ListeBenebutton.setOnAction(event -> handleListeBene());
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidTelephone(String telephone) {
        return TELEPHONE_PATTERN.matcher(telephone).matches();
    }

    private boolean isValidNumber(String number) {
        return NUMBER_PATTERN.matcher(number).matches();
    }

    @FXML
    private void handleSubmit() {
        // Validate all required fields
        if (NomTextField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Nom' est obligatoire.");
            return;
        }

        if (EmailTextField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Email' est obligatoire.");
            return;
        }

        if (!isValidEmail(EmailTextField.getText())) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide.");
            return;
        }

        if (TelephoneTextField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Téléphone' est obligatoire.");
            return;
        }

        if (!isValidTelephone(TelephoneTextField.getText())) {
            showAlert("Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        if (CauseTextField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Cause' est obligatoire.");
            return;
        }

        if (DescriptionTextArea.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le champ 'Description' est obligatoire.");
            return;
        }

        if (!ValeurTextField.getText().isEmpty() && !isValidNumber(ValeurTextField.getText())) {
            showAlert("Erreur", "La valeur demandée doit être un nombre valide.");
            return;
        }

        beneficiaires b = new beneficiaires();
        b.setNom(NomTextField.getText());
        b.setEmail(EmailTextField.getText());
        b.setTelephone(TelephoneTextField.getText());
        b.setEstElleAssociation(AssociationChoice.getValue());
        b.setCause(CauseTextField.getText());

        try {
            if (!ValeurTextField.getText().isEmpty()) {
                b.setValeurDemande(Double.parseDouble(ValeurTextField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La valeur demandée doit être un nombre valide");
            return;
        }

        b.setDescription(DescriptionTextArea.getText());

        ServicesBeneficiaires s = new ServicesBeneficiaires();
        s.add(b);

        // Show success message
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Votre demande a été soumise avec succès !");
        successAlert.showAndWait();

        // Clear all fields
        clearFields();
    }

    private void clearFields() {
        NomTextField.clear();
        EmailTextField.clear();
        TelephoneTextField.clear();
        AssociationChoice.setValue("Non");
        CauseTextField.clear();
        ValeurTextField.clear();
        DescriptionTextArea.clear();
    }

    @FXML
    private void handleListeBene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeBeneficiaires.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ListeBenebutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
            FXMLLoader loader = new FXMLLoader(AddBeneficiaireController.class.getResource("/AddBeneficiaire.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ajouter un Bénéficiaire");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la fenêtre d'ajout de bénéficiaire: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourHome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }


