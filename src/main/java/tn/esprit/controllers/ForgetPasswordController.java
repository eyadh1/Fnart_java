package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import tn.esprit.services.UserService;

import java.io.IOException;

public class ForgetPasswordController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button resetButton;
    
    @FXML
    private Label errorLabel;
    
    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
        resetButton.setOnAction(event -> handleResetPassword());
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (newPassword.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters long");
            return;
        }

        try {
            boolean success = userService.resetPassword(email, newPassword);
            
            if (success) {
                showAlert("Success", "Password has been reset successfully!", AlertType.INFORMATION);
                navigateToLogin();
            } else {
                errorLabel.setText("Email not found or password reset failed");
            }
        } catch (Exception e) {
            errorLabel.setText("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) resetButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load login page: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
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