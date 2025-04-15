package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import tn.esprit.enumerations.Role;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignUpController implements Initializable {

    @FXML
    private PasswordField PasswordField;

    @FXML
    private TextField PhoneField;

    @FXML
    private ChoiceBox<Role> RoleComboBox;

    @FXML
    private Button SignupButton;

    @FXML
    private RadioButton alphaRadio;

    @FXML
    private PasswordField confimPasswordField;

    @FXML
    private RadioButton deltaRadio;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField nameTF;

    private ToggleGroup genderToggleGroup;
    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserService();

        // Initialize role combo box
        RoleComboBox.getItems().addAll(Role.values());

        // Initialize gender toggle group
        genderToggleGroup = new ToggleGroup();
        alphaRadio.setToggleGroup(genderToggleGroup);
        deltaRadio.setToggleGroup(genderToggleGroup);

        // Add input validation listeners
        addValidationListeners();

        // Set up sign up button action
        SignupButton.setOnAction(event -> handleSignUp());
    }

    private void addValidationListeners() {
        // Email validation
        emailTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidEmail(newValue)) {
                emailTF.setStyle("-fx-border-color: red;");
            } else {
                emailTF.setStyle("-fx-border-color: green;");
            }
        });

        // Phone validation
        PhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidPhone(newValue)) {
                PhoneField.setStyle("-fx-border-color: red;");
            } else {
                PhoneField.setStyle("-fx-border-color: green;");
            }
        });

        // Password validation
        PasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidPassword(newValue)) {
                PasswordField.setStyle("-fx-border-color: red;");
            } else {
                PasswordField.setStyle("-fx-border-color: green;");
            }
        });

        // Confirm password validation
        confimPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(PasswordField.getText())) {
                confimPasswordField.setStyle("-fx-border-color: red;");
            } else {
                confimPasswordField.setStyle("-fx-border-color: green;");
            }
        });
    }

    private void handleSignUp() {
        if (!validateInputs()) {
            return;
        }

        User newUser = new User();
        newUser.setNom(nameTF.getText());
        newUser.setEmail(emailTF.getText());
        newUser.setPassword(PasswordField.getText());
        newUser.setPhone(PhoneField.getText());

        // Get selected gender
        RadioButton selectedRadio = (RadioButton) genderToggleGroup.getSelectedToggle();
        newUser.setGender(selectedRadio != null ? selectedRadio.getText() : "other");

        newUser.setRole(RoleComboBox.getValue());

        boolean success = userService.signUp(newUser);

        if (success) {
            showAlert("Success", "User registered successfully!", AlertType.INFORMATION);
            clearFields();
        } else {
            showAlert("Error", "Failed to register user. Email might already exist.", AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (nameTF.getText().isEmpty()) {
            showAlert("Error", "Full name is required", AlertType.ERROR);
            return false;
        }

        if (!isValidEmail(emailTF.getText())) {
            showAlert("Error", "Invalid email format", AlertType.ERROR);
            return false;
        }

        if (!isValidPassword(PasswordField.getText())) {
            showAlert("Error", "Password must be at least 8 characters long", AlertType.ERROR);
            return false;
        }

        if (!PasswordField.getText().equals(confimPasswordField.getText())) {
            showAlert("Error", "Passwords do not match", AlertType.ERROR);
            return false;
        }

        if (!isValidPhone(PhoneField.getText())) {
            showAlert("Error", "Invalid phone number", AlertType.ERROR);
            return false;
        }

        if (genderToggleGroup.getSelectedToggle() == null) {
            showAlert("Error", "Please select a gender", AlertType.ERROR);
            return false;
        }

        if (RoleComboBox.getValue() == null) {
            showAlert("Error", "Please select a role", AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{8,15}$";
        return Pattern.matches(phoneRegex, phone);
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nameTF.clear();
        emailTF.clear();
        PasswordField.clear();
        confimPasswordField.clear();
        PhoneField.clear();
        genderToggleGroup.selectToggle(null);
        RoleComboBox.getSelectionModel().clearSelection();
    }
}