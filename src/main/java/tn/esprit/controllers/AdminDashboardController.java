package tn.esprit.controllers;

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
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import tn.esprit.models.User;
import tn.esprit.services.UserService;
import tn.esprit.enumerations.Role;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // Pending Users Table View Components
    @FXML private TableView<User> pendingUsersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Role> roleColumn;
    @FXML private TableColumn<User, String> phoneColumn;

    // All Users Table View Components
    @FXML private TableView<User> allUsersTable;
    @FXML private TableColumn<User, String> allNameColumn;
    @FXML private TableColumn<User, String> allEmailColumn;
    @FXML private TableColumn<User, Role> allRoleColumn;
    @FXML private TableColumn<User, String> allStatusColumn;
    @FXML private TableColumn<User, String> allPhoneColumn;
    @FXML private TableColumn<User, String> allGenderColumn;

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

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadAllUsers();
        loadPendingUsers();
        initializeStatistics();
        initializeChart();
        setupSearchListener();
        setupTableSelectionListeners();
        addActivityLog("Admin dashboard initialized");
    }

    private void setupTableColumns() {
        // Setup Pending Users Table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Setup All Users Table columns
        allNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        allEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        allRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        allStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        allPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        allGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        // Make columns resize with table width
        pendingUsersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        allUsersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupTableSelectionListeners() {
        // Add listener to enable/disable approve and reject buttons based on selection
        pendingUsersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            if (approveButton != null) approveButton.setDisable(!hasSelection);
            if (rejectButton != null) rejectButton.setDisable(!hasSelection);
        });

        // Add listener for the all users table to enable/disable update and delete buttons
        allUsersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            if (updateButton != null) updateButton.setDisable(!hasSelection);
            if (deleteButton != null) deleteButton.setDisable(!hasSelection);
        });

        // Initially disable buttons until a selection is made
        if (approveButton != null) approveButton.setDisable(true);
        if (rejectButton != null) rejectButton.setDisable(true);
        if (updateButton != null) updateButton.setDisable(true);
        if (deleteButton != null) deleteButton.setDisable(true);
    }

    private void loadAllUsers() {
        ObservableList<User> allUsers = FXCollections.observableArrayList(userService.getAll());
        allUsersTable.setItems(allUsers);
    }

    private void loadPendingUsers() {
        ObservableList<User> pendingUsers = FXCollections.observableArrayList(userService.getPendingUsers());
        pendingUsersTable.setItems(pendingUsers);

        // Update button states
        boolean hasItems = !pendingUsers.isEmpty();
        if (pendingUsersTable.getSelectionModel().getSelectedItem() == null && hasItems) {
            pendingUsersTable.getSelectionModel().selectFirst();
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

        // Set colors for the chart segments
        applyCustomChartColors(pieChartData);
    }

    private void applyCustomChartColors(ObservableList<PieChart.Data> pieChartData) {
        String[] colors = {
                "-fx-pie-color: #3498db;", // Blue
                "-fx-pie-color: #2ecc71;", // Green
                "-fx-pie-color: #e74c3c;", // Red
                "-fx-pie-color: #f39c12;"  // Orange
        };

        for (int i = 0; i < pieChartData.size(); i++) {
            pieChartData.get(i).getNode().setStyle(colors[i % colors.length]);
        }
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadAllUsers();
                loadPendingUsers();
            } else {
                allUsersTable.setItems(FXCollections.observableArrayList(userService.searchUsers(newValue)));
                pendingUsersTable.setItems(FXCollections.observableArrayList(userService.searchPendingUsers(newValue)));
            }
        });
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userService.approveUser(selectedUser.getId())) {
                addActivityLog("Approved user: " + selectedUser.getEmail());
                refreshDashboard();
                showAlert("User Approved", "User " + selectedUser.getNom() + " has been approved successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to approve user: " + selectedUser.getEmail(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a user to approve.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Rejection");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to reject user " + selectedUser.getNom() + "? This action cannot be undone.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");

            confirmAlert.getButtonTypes().setAll(yesButton, noButton);
            confirmAlert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    if (userService.rejectUser(selectedUser.getId())) {
                        addActivityLog("Rejected user: " + selectedUser.getEmail());
                        refreshDashboard();
                        showAlert("User Rejected", "User " + selectedUser.getNom() + " has been rejected and removed from the system.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to reject user: " + selectedUser.getEmail(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("No Selection", "Please select a user to reject.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        User selectedUser = allUsersTable.getSelectionModel().getSelectedItem();
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
        User selectedUser = allUsersTable.getSelectionModel().getSelectedItem();
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

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
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

    private void addActivityLog(String message) {
        activityList.getItems().add(0, LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + message);
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
}