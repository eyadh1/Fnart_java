package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tn.esprit.services.UserService;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class ResetPasswordController {
    @FXML
    private TextField verificationCodeField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button resetButton;

    private UserService userService;
    private String email;

    @FXML
    public void initialize() {
        userService = new UserService();
        errorLabel.setVisible(false);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleResetPassword() {
        String verificationCode = verificationCodeField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (verificationCode.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = userService.resetPassword(verificationCode, newPassword);
        if (success) {
            showAlert("Success", "Your password has been updated successfully.", AlertType.INFORMATION);
            showLoginScreen();
        } else {
            errorLabel.setText("Invalid verification code or code has expired.");
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) resetButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error returning to login screen", AlertType.ERROR);
        }
    }

    // Method to create and show the reset password window
    public static void showResetPasswordWindow(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(ResetPasswordController.class.getResource("/fxml/ResetPassword.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(scene);

            ResetPasswordController controller = loader.getController();
            controller.setEmail(email);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
} 