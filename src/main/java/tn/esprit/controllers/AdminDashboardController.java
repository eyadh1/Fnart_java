package tn.esprit.controllers;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.stage.Screen;
import javafx.geometry.Insets;
import tn.esprit.components.UserCard;
import tn.esprit.models.User;
import tn.esprit.services.UserService;
import tn.esprit.enumerations.Role;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import tn.esprit.utils.SessionManager;

public class AdminDashboardController implements Initializable {

    // Statistics Components
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label pendingUsersLabel;
    @FXML private PieChart userRoleChart;
    @FXML private ListView<String> activityList;
    @FXML private Label lastUpdatedLabel;

    // Buttons
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    // Search Field
    @FXML private TextField searchField;

    // Profile components
    @FXML private VBox profilePanel;
    @FXML private VBox profileContainer;
    @FXML private ImageView profileImage;
    @FXML private ImageView profileImageLarge;
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;

    // Card-based user containers
    @FXML private VBox allUsersCardContainer;
    @FXML private VBox pendingUsersCardContainer;

    private final UserService userService = new UserService();

    // Store selected pending user
    private HBox selectedPendingCard = null;
    private User selectedPendingUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAllUsers();
        loadPendingUsers();
        initializeStatistics();
        initializeChart();
        setupSearchListener();
        initializeProfileSection();
        addActivityLog("Admin dashboard initialized");
    }

    private void initializeProfileSection() {
        // Set up the admin info
        User currentAdmin = userService.getCurrentAdmin();
        if (currentAdmin != null) {
            adminNameLabel.setText(currentAdmin.getNom());
            adminEmailLabel.setText(currentAdmin.getEmail());
        }
    }

    @FXML
    private void toggleProfilePanel() {
        profilePanel.setVisible(!profilePanel.isVisible());
        profilePanel.setManaged(profilePanel.isVisible());
    }

    //  handling edit profile
    @FXML
    private void handleEditProfile() {
        // Create a dialog to edit the admin's profile
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Get current admin info
        User currentAdmin = userService.getCurrentAdmin(); // You'd need to implement this method

        // Create fields pre-filled with current admin data
        TextField nameField = new TextField(currentAdmin != null ? currentAdmin.getNom() : "Admin User");
        TextField emailField = new TextField(currentAdmin != null ? currentAdmin.getEmail() : "admin@arttherapy.com");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");

        // Add image upload option if needed
        Button uploadImageButton = new Button("Upload Profile Picture");
        uploadImageButton.setOnAction(e -> {
            // Implement file chooser for profile picture
            // This is just a placeholder
            showAlert("Upload", "Profile picture upload functionality would go here", Alert.AlertType.INFORMATION);
        });

        // Add fields to the grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("New Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(uploadImageButton, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Process the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                // Update admin profile logic would go here
                // This is just a placeholder
                adminNameLabel.setText(nameField.getText());
                adminEmailLabel.setText(emailField.getText());

                addActivityLog("Updated admin profile");
                showAlert("Profile Updated", "Your profile has been updated successfully.", Alert.AlertType.INFORMATION);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private HBox createUserCard(User user, boolean isPending) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.07), 5, 0, 0, 1);");
        card.setMinHeight(60);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1.7976931348623157E308);

        // Profile image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            imageView.setImage(new Image(user.getProfilePicture(), true));
        } else {
            URL defaultImgUrl = getClass().getResource("/assets/default-profile.png");
            if (defaultImgUrl != null) {
                imageView.setImage(new Image(defaultImgUrl.toExternalForm()));
            } else {
                imageView.setImage(new Image("https://ui-avatars.com/api/?name=User"));
            }
        }
        imageView.setStyle("-fx-background-radius: 50%;");

        // User info
        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(user.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        Label subtitleLabel = new Label(user.getEmail());
        subtitleLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
        infoBox.getChildren().addAll(nameLabel, subtitleLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action button (always at right)
        Button actionButton = new Button("Voir le profil");
        actionButton.setStyle("-fx-background-color: #e7f3ff; -fx-text-fill: #1877f2; -fx-background-radius: 8;");
        actionButton.setOnAction(e -> openUserProfile(user));

        card.getChildren().addAll(imageView, infoBox, spacer, actionButton);

        // If this is a pending user card, add selection logic
        if (isPending) {
            card.setOnMouseClicked(e -> {
                // Remove highlight from previous
                if (selectedPendingCard != null) {
                    selectedPendingCard.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.07), 5, 0, 0, 1);");
                }
                // Highlight this card
                card.setStyle("-fx-background-color: #e7f3ff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, #1877f2, 8, 0, 0, 1);");
                selectedPendingCard = card;
                selectedPendingUser = user;
                if (approveButton != null) approveButton.setDisable(false);
                if (rejectButton != null) rejectButton.setDisable(false);
            });
        }
        return card;
    }

    private void loadAllUsers() {
        ObservableList<User> allUsers = FXCollections.observableArrayList(userService.getAll());
        allUsersCardContainer.getChildren().clear();
        for (User user : allUsers) {
            allUsersCardContainer.getChildren().add(createUserCard(user, false));
        }
    }

    private void loadPendingUsers() {
        ObservableList<User> pendingUsers = FXCollections.observableArrayList(userService.getPendingUsers());
        pendingUsersCardContainer.getChildren().clear();
        selectedPendingCard = null;
        selectedPendingUser = null;
        if (approveButton != null) approveButton.setDisable(true);
        if (rejectButton != null) rejectButton.setDisable(true);
        for (User user : pendingUsers) {
            pendingUsersCardContainer.getChildren().add(createUserCard(user, true));
        }
    }

    private void initializeStatistics() {
        totalUsersLabel.setText(String.valueOf(userService.getAll().size()));
        activeUsersLabel.setText(String.valueOf(userService.getActiveUsersCount()));
        pendingUsersLabel.setText(String.valueOf(userService.getPendingUsers().size()));
        lastUpdatedLabel.setText("Last updated: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private void initializeChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Regular Users", userService.getUserCountByRole(Role.REGULARUSER)),
                new PieChart.Data("Artists", userService.getUserCountByRole(Role.ARTIST)),
                new PieChart.Data("Therapists", userService.getUserCountByRole(Role.THERAPIST)),
                new PieChart.Data("Admins", userService.getUserCountByRole(Role.ADMIN))
        );
        userRoleChart.setData(pieChartData);
        userRoleChart.setLegendVisible(true);
        applyCustomChartColors(pieChartData);
    }

    private void applyCustomChartColors(ObservableList<PieChart.Data> pieChartData) {
        // No manual color setting; CSS will handle the 3D gradients
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = (newValue == null) ? "" : newValue.trim().toLowerCase();

            // Filter all users
            ObservableList<User> filteredAllUsers = FXCollections.observableArrayList(
                    userService.getAll().stream()
                            .filter(u -> u.getNom().toLowerCase().contains(searchTerm) ||
                                    u.getEmail().toLowerCase().contains(searchTerm))
                            .toList()
            );

            // Filter pending users
            ObservableList<User> filteredPendingUsers = FXCollections.observableArrayList(
                    userService.getPendingUsers().stream()
                            .filter(u -> u.getNom().toLowerCase().contains(searchTerm) ||
                                    u.getEmail().toLowerCase().contains(searchTerm))
                            .toList()
            );

            allUsersCardContainer.getChildren().clear();
            for (User user : filteredAllUsers) {
                allUsersCardContainer.getChildren().add(createUserCard(user, false));
            }
            pendingUsersCardContainer.getChildren().clear();
            for (User user : filteredPendingUsers) {
                pendingUsersCardContainer.getChildren().add(createUserCard(user, true));
            }
        });
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadAllUsers();
            loadPendingUsers();
            return;
        }

        // Filter with exact same logic as the listener
        ObservableList<User> filteredAllUsers = FXCollections.observableArrayList(
                userService.getAll().stream()
                        .filter(u -> u.getNom().toLowerCase().contains(searchTerm) ||
                                u.getEmail().toLowerCase().contains(searchTerm))
                        .toList()
        );

        ObservableList<User> filteredPendingUsers = FXCollections.observableArrayList(
                userService.getPendingUsers().stream()
                        .filter(u -> u.getNom().toLowerCase().contains(searchTerm) ||
                                u.getEmail().toLowerCase().contains(searchTerm))
                        .toList()
        );

        allUsersCardContainer.getChildren().clear();
        for (User user : filteredAllUsers) {
            allUsersCardContainer.getChildren().add(createUserCard(user, false));
        }
        pendingUsersCardContainer.getChildren().clear();
        for (User user : filteredPendingUsers) {
            pendingUsersCardContainer.getChildren().add(createUserCard(user, true));
        }
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        User userToApprove = selectedPendingUser;
        if (userToApprove != null) {
            if (userService.approveUser(userToApprove.getId())) {
                addActivityLog("Approved user: " + userToApprove.getEmail());
                refreshDashboard();
                showAlert("User Approved", "User " + userToApprove.getNom() + " has been approved successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to approve user: " + userToApprove.getEmail(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a user to approve.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        User userToReject = selectedPendingUser;
        if (userToReject != null) {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Rejection");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to reject user " + userToReject.getNom() + "? This action cannot be undone.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");

            confirmAlert.getButtonTypes().setAll(yesButton, noButton);
            confirmAlert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    if (userService.rejectUser(userToReject.getId())) {
                        addActivityLog("Rejected user: " + userToReject.getEmail());
                        refreshDashboard();
                        showAlert("User Rejected", "User " + userToReject.getNom() + " has been rejected and removed from the system.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to reject user: " + userToReject.getEmail(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("No Selection", "Please select a user to reject.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        User selectedUser = allUsersCardContainer.getChildren().stream()
                .filter(node -> node instanceof UserCard)
                .map(node -> (UserCard) node)
                .findFirst()
                .map(UserCard::getUser)
                .orElse(null);
        if (selectedUser != null) {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete user " + selectedUser.getNom() + "? This action cannot be undone.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");

            confirmAlert.getButtonTypes().setAll(yesButton, noButton);
            confirmAlert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    if (userService.deleteUser(selectedUser.getId())) {
                        addActivityLog("Deleted user: " + selectedUser.getEmail());
                        refreshDashboard();
                        showAlert("User Deleted", "User " + selectedUser.getNom() + " has been deleted from the system.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to delete user: " + selectedUser.getEmail(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("No Selection", "Please select a user to delete.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        User selectedUser = allUsersCardContainer.getChildren().stream()
                .filter(node -> node instanceof UserCard)
                .map(node -> (UserCard) node)
                .findFirst()
                .map(UserCard::getUser)
                .orElse(null);
        if (selectedUser != null) {
            // Create a dialog to update user information
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Update User");
            dialog.setHeaderText("Update information for " + selectedUser.getNom());

            // Set the button types
            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Create fields pre-filled with current user data
            TextField nameField = new TextField(selectedUser.getNom());
            TextField emailField = new TextField(selectedUser.getEmail());
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Leave blank to keep current password");
            TextField phoneField = new TextField(selectedUser.getPhone());

            // Role dropdown
            ComboBox<Role> roleComboBox = new ComboBox<>();
            roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
            roleComboBox.setValue(selectedUser.getRole());

            // Gender dropdown
            ComboBox<String> genderComboBox = new ComboBox<>();
            genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
            genderComboBox.setValue(selectedUser.getGender());

            // Status dropdown
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(FXCollections.observableArrayList("ACTIVE", "PENDING"));
            statusComboBox.setValue(selectedUser.getStatus());

            // Add fields to the grid
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);
            grid.add(new Label("Password:"), 0, 2);
            grid.add(passwordField, 1, 2);
            grid.add(new Label("Phone:"), 0, 3);
            grid.add(phoneField, 1, 3);
            grid.add(new Label("Role:"), 0, 4);
            grid.add(roleComboBox, 1, 4);
            grid.add(new Label("Gender:"), 0, 5);
            grid.add(genderComboBox, 1, 5);
            grid.add(new Label("Status:"), 0, 6);
            grid.add(statusComboBox, 1, 6);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a user when the update button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    // Create a copy of the selected user with updated fields
                    User updatedUser = new User();
                    updatedUser.setId(selectedUser.getId());
                    updatedUser.setNom(nameField.getText());
                    updatedUser.setEmail(emailField.getText());

                    // Only update password if a new one is provided
                    if (!passwordField.getText().isEmpty()) {
                        updatedUser.setPassword(passwordField.getText());
                    } else {
                        updatedUser.setPassword(selectedUser.getPassword());
                    }

                    updatedUser.setPhone(phoneField.getText());
                    updatedUser.setRole(roleComboBox.getValue());
                    updatedUser.setGender(genderComboBox.getValue());
                    updatedUser.setStatus(statusComboBox.getValue());

                    return updatedUser;
                }
                return null;
            });

            // Show the dialog and process the result
            dialog.showAndWait().ifPresent(updatedUser -> {
                // Check if password was changed
                boolean passwordChanged = !passwordField.getText().isEmpty();

                // If password changed, we need to hash it before updating
                if (passwordChanged) {
                    // Password is already stored as plain text in the updatedUser object
                    // UserService.update will handle the hashing
                } else {
                    // Use the existing password hash
                    updatedUser.setPassword(selectedUser.getPassword());
                }

                // Update the user in the database
                userService.update(updatedUser);

                addActivityLog("Updated user: " + updatedUser.getEmail());
                refreshDashboard();
                showAlert("User Updated", "User " + updatedUser.getNom() + " has been updated successfully.", Alert.AlertType.INFORMATION);
            });
        } else {
            showAlert("No Selection", "Please select a user to update.", Alert.AlertType.WARNING);
        }
    }

    private boolean confirmAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText(message);
        alert.setContentText("This cannot be undone.");
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear the user session
            SessionManager.clearSession();

            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Make the scene responsive
            makeSceneResponsive(scene, stage);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load login screen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshDashboard();
        addActivityLog("Dashboard refreshed");
        showAlert("Refreshed", "Dashboard data has been refreshed.", Alert.AlertType.INFORMATION);
    }

    private void refreshDashboard() {
        loadAllUsers();
        loadPendingUsers();
        initializeStatistics();
        initializeChart();
    }

    // Update the addActivityLog method
    private void addActivityLog(String message) {
        String logEntry = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + message;

        // Add to the top of the list
        activityList.getItems().add(0, logEntry);

        // Optional: Apply fade-in animation to the first cell
        if (activityList.lookup(".list-cell") != null) {
            Node cell = activityList.lookup(".list-cell");
            FadeTransition ft = new FadeTransition(Duration.millis(500), cell);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }

        // Limit the log size to prevent memory issues (optional)
        if (activityList.getItems().size() > 100) {
            activityList.getItems().remove(100, activityList.getItems().size());
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void openUserProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();
            // Get the controller and pass the user
            tn.esprit.controllers.ProfileController profileController = loader.getController();
            profileController.setUser(user); // You need to implement setUser(User user) in ProfileController
            // Show in a new window
            Stage stage = new Stage();
            stage.setTitle("Profil de l'utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            showAlert("Erreur", "Impossible d'ouvrir le profil: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}