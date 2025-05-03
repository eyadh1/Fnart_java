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

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
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

    @FXML
    private TextField emailField; // Ensure this is annotated with @FXML

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
            } else {
                String email = emailField.getText();
                if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
                    showAlert("Validation Error", "Veuillez fournir une adresse email valide.");
                    return;
                }

                // Send email with donation details
                String htmlContent = "<html>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "</head>" +
                        "<body style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; color: #333; line-height: 1.6; background-color: #f9f9f9;'>" +
                        "<div style='background-color: white; border-radius: 12px; padding: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.05);'>" +
                        "<div style='text-align: center; margin-bottom: 25px;'>" +
                        "<h1 style='font-size: 26px; font-weight: 600; color: #3498DB; margin: 0;'>✨ Merci pour votre générosité ✨</h1>" +
                        "<div style='width: 80px; height: 3px; background: linear-gradient(90deg, #3498DB, #9B59B6); margin: 15px auto;'></div>" +
                        "</div>" +

                        "<p style='font-size: 16px; margin-bottom: 20px;'>Bonjour,</p>" +

                        "<p style='font-size: 16px; margin-bottom: 10px;'>Nous sommes touchés par votre contribution et tenons à vous remercier chaleureusement pour votre don :</p>" +

                        "<div style='background: linear-gradient(135deg, #E8F8F5, #D1F2EB); border-left: 4px solid #28B463; padding: 15px; border-radius: 6px; margin: 20px 0;'>" +
                        "<p style='font-size: 20px; font-weight: 600; color: #1E8449; margin: 0; text-align: center;'>" + don.getType() + "</p>" +
                        "</div>" +

                        "<p style='font-size: 16px; margin-bottom: 25px;'>Votre soutien fait une réelle différence et contribue directement à améliorer la vie de ceux qui en ont le plus besoin. 💖</p>" +

                        "<div style='background-color: #F5F5F5; border-radius: 8px; padding: 20px; margin: 25px 0;'>" +
                        "<h2 style='font-size: 18px; color: #34495E; margin-top: 0; display: flex; align-items: center;'>" +
                        "<span style='margin-right: 10px;'>📞</span> Prochaines étapes" +
                        "</h2>" +
                        "<p style='margin-bottom: 15px;'>Pour organiser la récupération de votre don, veuillez contacter notre responsable :</p>" +

                        "<div style='display: flex; flex-direction: column; gap: 8px;'>" +
                        "<div style='display: flex; align-items: center;'>" +
                        "<span style='width: 20px; margin-right: 10px; text-align: center;'>👤</span>" +
                        "<span style='font-weight: 600;'>Mme Yasmine Trabelsi</span>" +
                        "</div>" +
                        "<div style='display: flex; align-items: center;'>" +
                        "<span style='width: 20px; margin-right: 10px; text-align: center;'>📱</span>" +
                        "<span style='font-weight: 600;'>+216 55 123 456</span>" +
                        "</div>" +
                        "<div style='display: flex; align-items: center;'>" +
                        "<span style='width: 20px; margin-right: 10px; text-align: center;'>✉️</span>" +
                        "<span style='font-weight: 600;'>responsable.dons@fnart.com</span>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +

                        "<p style='font-size: 16px; margin-top: 30px;'>Cordialement,</p>" +
                        "<p style='font-size: 16px; font-weight: 600; color: #3498DB; margin-bottom: 5px;'>L'équipe de Fnart</p>" +

                        "<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eaeaea;'>" +
                        "<p style='color: #7F8C8D; font-size: 14px;'>Merci de faire partie de notre communauté solidaire</p>" +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
                sendEmail(email, "Détails du don", htmlContent);
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private void sendEmail(String to, String subject, String bodyHtml) {
        String from = "eyadhiflaouii@gmail.com"; // Replace with your email
        String password = "ayrt opol imsy wtga"; // App password (NEVER hardcode in production)

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(bodyHtml, "text/html; charset=utf-8"); // Send as HTML

            Transport.send(message);
        } catch (MessagingException e) {
            showAlert("Erreur Email", "Échec de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void clearFields() {
        ValeurTextField.clear();
        TypeChoice.setValue("Argent");
        DescriptionTextArea.clear();
        BeneficiaireChoice.setValue(null);
        emailField.clear();
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