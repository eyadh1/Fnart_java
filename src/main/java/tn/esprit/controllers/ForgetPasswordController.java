package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import tn.esprit.services.UserService;
import tn.esprit.services.EmailService;

import java.io.IOException;
import java.util.Random;

public class ForgetPasswordController {
    @FXML
    private TextField emailTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Label backToLoginLabel;
    @FXML
    private Label errorLabel;
    
    private UserService userService;
    private EmailService emailService;

    @FXML
    public void initialize() {
        userService = new UserService();
        emailService = new EmailService();
        sendButton.setOnAction(event -> handleSend());
        backToLoginLabel.setOnMouseClicked(event -> navigateToLogin());
    }

    private void handleSend() {
        String email = emailTextField.getText();
        if (email == null || email.isEmpty()) {
            showError("Please enter your email address");
            return;
        }
        try {
            if (!userService.emailExists(email)) {
                showError("Email not found in our system");
                return;
            }
            // Generate a 4-digit verification code
            String verificationCode = generateVerificationCode();
            userService.saveResetToken(email, verificationCode);
            
            // Send email with the code
            String emailContent = "<html><body>"
                    + "<p>You requested a password reset. Here is your verification code:</p>"
                    + "<h2 style='color: #7C4DFF; font-size: 24px;'>" + verificationCode + "</h2>"
                    + "<p>Please enter this code in the verification form.</p>"
                    + "<p>If you did not request this, please ignore this email.</p>"
                    + "</body></html>";
            EmailService.sendHtmlEmail(email, "Reset Your Password", emailContent);
            
            // Show the verification code form
            showVerificationCodeForm(email, verificationCode);
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // 4-digit code
        return String.valueOf(code);
    }

    private void showVerificationCodeForm(String email, String verificationCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/verification_code.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the data
            VerificationCodeController controller = loader.getController();
            controller.setData(email, verificationCode);
            
            // Show the verification code scene
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Failed to load verification code form.");
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) backToLoginLabel.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            showError("Failed to load login screen.");
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        } else {
            showAlert("Error", message, AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 