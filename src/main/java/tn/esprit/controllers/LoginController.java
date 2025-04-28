package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.enumerations.Role;
import tn.esprit.models.User;
import tn.esprit.services.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static tn.esprit.enumerations.Role.ADMIN;
import static tn.esprit.enumerations.Role.ARTIST;

public class LoginController implements Initializable {

    @FXML
    private TextField emailTF;
    @FXML
    private PasswordField PasswordField;
    @FXML
    private Hyperlink SignupLink;
    @FXML
    private Hyperlink forgetPasswordLink;
    @FXML
    private Button LoginButton;
    @FXML
    private Text errorMessage;
    @FXML
    private Text successMessage;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize messages
        errorMessage.setVisible(false);
        successMessage.setVisible(false);

        // Set up event handlers
        SignupLink.setOnAction(event -> navigateToSignup());
        forgetPasswordLink.setOnAction(event -> navigateToForgetPassword());
        LoginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailTF.getText().trim();
        String password = PasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        try {
            User authenticatedUser = userService.login(email, password);

            if (authenticatedUser != null) {
                redirectBasedOnRole(authenticatedUser);
            } else {
                showError("Invalid credentials or account not active");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
        }
    }

    private void redirectBasedOnRole(User user) {
        try {
            String fxmlPath = determineDashboardPath(user.getRole());
            String title = determineDashboardTitle(user.getRole());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Set the user in the controller
            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).setUser(user);
            }

            Stage stage = (Stage) emailTF.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            showError("Failed to load dashboard. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String determineDashboardPath(Role role) {
        return switch (role) {
            case ADMIN -> "/AdminDashboard.fxml";
            case ARTIST -> "/ArtistDashboard.fxml";
            case THERAPIST -> "/TherapistDashboard.fxml";
            default -> "/profile.fxml";
        };
    }

    private String determineDashboardTitle(Role role) {
        return switch (role) {
            case ADMIN -> "Admin Dashboard";
            case ARTIST -> "Artist Dashboard";
            case THERAPIST -> "Therapist Dashboard";
            default -> "User Dashboard";
        };
    }


    private void showError(String message) {
        Platform.runLater(() -> {
            successMessage.setVisible(false);
            errorMessage.setText(message);
            errorMessage.setVisible(true);
            errorMessage.setStyle("-fx-fill: red; -fx-font-weight: bold;");

            // Auto-hide after 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(event -> errorMessage.setVisible(false));
            pause.play();
        });
    }

    private void showSuccess(String message) {
        Platform.runLater(() -> {
            errorMessage.setVisible(false);
            successMessage.setText(message);
            successMessage.setVisible(true);
            successMessage.setStyle("-fx-fill: green; -fx-font-weight: bold;");

            // Auto-hide after 3 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> successMessage.setVisible(false));
            pause.play();
        });
    }

    private void navigateToSignup() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Signup.fxml"));
            Stage stage = (Stage) SignupLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page d'inscription: " + e.getMessage());
        }
    }

    private void navigateToForgetPassword() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/forgetPassword.fxml"));
            Stage stage = (Stage) forgetPasswordLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page de récupération: " + e.getMessage());
        }
    }
}



interface UserAwareController {
    void setUser(User user);
}



