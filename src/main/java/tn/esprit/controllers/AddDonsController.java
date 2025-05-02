package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.models.Beneficiaires;
import tn.esprit.models.Dons;
import tn.esprit.services.ServicesBeneficiaires;
import tn.esprit.services.ServicesDons;
import com.stripe.Stripe;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import tn.esprit.services.StripeService;


public class AddDonsController implements Initializable {
    @FXML
    private Button retourHome;
    @FXML
    private TextField ValeurTextField;

    @FXML
    private ChoiceBox<String> TypeChoice;

    @FXML
    private TextArea DescriptionTextArea;

    @FXML
    private ChoiceBox<Beneficiaires> BeneficiaireChoice;

    @FXML
    private Button AjoutButton;

    @FXML
    private WebView webView;

    @FXML
    private VBox formContainer;


    @FXML
    private Button ListeButton;

    @FXML
    private Button ListeBeneficiairesButton;

    @FXML
    private AnchorPane rootPane;


    private final ServicesDons servicesDons = new ServicesDons();
    private final ServicesBeneficiaires servicesBeneficiaires = new ServicesBeneficiaires();

    // Number validation pattern
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String cssPath = getClass().getResource("/css/pinterest-style.css").toExternalForm();
        System.out.println("CSS file found at: " + cssPath);
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
       /* ListeButton.setOnAction(event -> handleListe());*/

    }

    private boolean isValidNumber(String number) {
        return NUMBER_PATTERN.matcher(number).matches();
    }

    private void loadBeneficiaires() {
        try {
            List<Beneficiaires> beneficiairesList = servicesBeneficiaires.getAll();
            BeneficiaireChoice.getItems().clear();
            BeneficiaireChoice.getItems().addAll(beneficiairesList);

            // Set converter to display beneficiaire name in the choice box
            BeneficiaireChoice.setConverter(new StringConverter<Beneficiaires>() {
                @Override
                public String toString(Beneficiaires beneficiaire) {
                    return beneficiaire == null ? "" : beneficiaire.getNom();
                }

                @Override
                public Beneficiaires fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les bénéficiaires");
            e.printStackTrace();
        }
    }



    @FXML
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

            Beneficiaires selectedBeneficiaire = BeneficiaireChoice.getValue();
            if (selectedBeneficiaire == null) {
                showAlert("Erreur", "Veuillez sélectionner un bénéficiaire");
                return;
            }

            // Create new don
            Dons don = new Dons();
            don.setValeur(Double.parseDouble(ValeurTextField.getText()));
            don.setType(TypeChoice.getValue());
            don.setDescription(DescriptionTextArea.getText());
            don.setBeneficiaire(selectedBeneficiaire);

            // Add to database
            if (don.getType().equalsIgnoreCase("Argent")) {
                try {
                    String checkoutUrl = StripeService.createCheckoutSession(don.getValeur(), don.getDescription());

                    // Hide form, show WebView
                    formContainer.setVisible(false);
                    formContainer.setManaged(false);
                    webView.setVisible(true);
                    webView.setManaged(true);

                    WebEngine engine = webView.getEngine();
                    engine.load(checkoutUrl);

                    // Listen for Stripe redirect
                    engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                        if (newLoc.contains("/success")) {
                            showToast("✅ Paiement réussi ! Merci pour votre don ❤️", true);
                            webView.setVisible(false);
                            webView.setManaged(false);
                            formContainer.setVisible(true);
                            formContainer.setManaged(true);
                            clearFields(); // optional
                        } else if (newLoc.contains("/cancel")) {
                            showToast("❌ Paiement annulé", false);
                            webView.setVisible(false);
                            webView.setManaged(false);
                            formContainer.setVisible(true);
                            formContainer.setManaged(true);
                        }
                    });

                    // Exit here to wait for Stripe
                    return;

                } catch (Exception e) {
                    showAlert("Erreur", "Échec du paiement Stripe.");
                    e.printStackTrace();
                    return;
                }
            }



            servicesDons.add(don);


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

    @FXML
    private void handleListe() {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/ListeDons.fxml"));
            rootPane.getChildren().removeAll();
            rootPane.getChildren().setAll(fxml);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleListeBeneficiaires() {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/ListeBeneficiaires.fxml"));
            rootPane.getChildren().removeAll();
            rootPane.getChildren().setAll(fxml);
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
            FXMLLoader loader = new FXMLLoader(AddDonsController.class.getResource("/AddDons.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Ajouter un Don");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la fenêtre d'ajout de don: " + e.getMessage());
            alert.showAndWait();
        }
    }


    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourHome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message, boolean success) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: " + (success ? "#4BB543" : "#D32F2F") + ";" +
                "-fx-text-fill: white;" +
                "-fx-padding: 12px 20px;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;");
        toast.setOpacity(0);

        // Center horizontally
        toast.setLayoutX((rootPane.getWidth() - 300) / 2);
        toast.setLayoutY(rootPane.getHeight() - 80); // 80px from bottom

        rootPane.getChildren().add(toast);

        // Fade in and out
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(javafx.util.Duration.seconds(2.5));

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(toast));

        fadeIn.play();
    }



}