package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import tn.esprit.enumerations.Role;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.UUID;
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

    @FXML
    private Circle profileImageCircle;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button uploadImageButton;

    @FXML
    private Hyperlink loginLink;

    private ToggleGroup genderToggleGroup;
    private UserService userService;
    private File selectedImageFile;
    private String imagePath;

    // Directory to save profile images
    private final String IMAGE_DIRECTORY = "src/main/resources/tn/esprit/assets/profiles/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserService();

        // Set up clip for profile image to make it circular
        profileImageView.setClip(new Circle(55, 55, 55));

        // Initialize role combo box
        RoleComboBox.getItems().addAll(Role.values());

        // Initialize gender toggle group
        genderToggleGroup = new ToggleGroup();
        alphaRadio.setToggleGroup(genderToggleGroup);
        deltaRadio.setToggleGroup(genderToggleGroup);

        // Add input validation listeners
        addValidationListeners();

        // Set up image upload button action
        uploadImageButton.setOnAction(event -> handleImageUpload());

        // Set up sign up button action
        SignupButton.setOnAction(event -> handleSignUp());

        // Set up login link action
        loginLink.setOnAction(event -> navigateToLogin());

        // Add styling classes to form elements
        applyStyles();
    }

    private void applyStyles() {
        // Add CSS classes to the form elements for styling
        nameTF.getStyleClass().add("modern-field");
        emailTF.getStyleClass().add("modern-field");
        PasswordField.getStyleClass().add("modern-field");
        confimPasswordField.getStyleClass().add("modern-field");
        PhoneField.getStyleClass().add("modern-field");
        RoleComboBox.getStyleClass().add("modern-choice");
        alphaRadio.getStyleClass().add("modern-radio");
        deltaRadio.getStyleClass().add("modern-radio");
    }

    private void navigateToLogin() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) loginLink.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }



    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                profileImageView.setImage(image);

                // Generate unique image name
                String uniqueID = UUID.randomUUID().toString();
                String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf('.'));
                imagePath = uniqueID + extension;
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage(), AlertType.ERROR);
            }
        }
    }

    private void addValidationListeners() {
        // Email validation
        emailTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidEmail(newValue)) {
                emailTF.setStyle("-fx-border-color: red; -fx-background-radius: 20; -fx-border-radius: 20;");
            } else {
                emailTF.setStyle("-fx-border-color: green; -fx-background-radius: 20; -fx-border-radius: 20;");
            }
        });

        // Phone validation
        PhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidPhone(newValue)) {
                PhoneField.setStyle("-fx-border-color: red; -fx-background-radius: 20; -fx-border-radius: 20;");
            } else {
                PhoneField.setStyle("-fx-border-color: green; -fx-background-radius: 20; -fx-border-radius: 20;");
            }
        });

        // Password validation
        PasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidPassword(newValue)) {
                PasswordField.setStyle("-fx-border-color: red; -fx-background-radius: 20; -fx-border-radius: 20;");
            } else {
                PasswordField.setStyle("-fx-border-color: green; -fx-background-radius: 20; -fx-border-radius: 20;");
            }
        });

        // Confirm password validation
        confimPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(PasswordField.getText())) {
                confimPasswordField.setStyle("-fx-border-color: red; -fx-background-radius: 20; -fx-border-radius: 20;");
            } else {
                confimPasswordField.setStyle("-fx-border-color: green; -fx-background-radius: 20; -fx-border-radius: 20;");
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

        // Save the profile image if one was selected
        if (selectedImageFile != null) {
            try {
                // Ensure directory exists
                Files.createDirectories(Paths.get(IMAGE_DIRECTORY));

                // Copy the image to our directory
                Path destination = Paths.get(IMAGE_DIRECTORY + imagePath);
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // You might want to store the image path in the user's record
                // This would require adding an 'imagePath' field to your User class and database
                // newUser.setImagePath(imagePath);
            } catch (IOException e) {
                showAlert("Error", "Failed to save profile image: " + e.getMessage(), AlertType.ERROR);
                return;
            }
        }

        boolean success = userService.signUp(newUser);

        if (success) {
            if (newUser.getRole() == Role.REGULARUSER) {
                showAlert("Success", "Account created successfully! You can now login.", AlertType.INFORMATION);
            } else {
                showAlert("Pending Approval",
                        "Your account requires admin approval. You'll be notified when your account is activated.",
                        AlertType.INFORMATION);
            }
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
        profileImageView.setImage(null);
        selectedImageFile = null;
        imagePath = null;
    }
}