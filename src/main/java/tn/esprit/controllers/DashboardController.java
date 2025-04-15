package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    @FXML private Button logoutButton;
    @FXML private Button usersButton;
    @FXML private Button pendingApprovalsButton;
    @FXML private Button statisticsButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private VBox usersView;
    @FXML private VBox pendingApprovalsView;
    @FXML private VBox statisticsView;
    @FXML private TableView<User> pendingTable;
    @FXML private TableColumn<User, Integer> pendingIdColumn;
    @FXML private TableColumn<User, String> pendingNameColumn;
    @FXML private TableColumn<User, String> pendingEmailColumn;
    @FXML private TableColumn<User, String> pendingRoleColumn;
    @FXML private TableColumn<User, Void> pendingActionsColumn;
    @FXML private Label totalUsersLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label statusLabel;

    private UserService userService;
    private ObservableList<User> usersList;
    private ObservableList<User> pendingUsersList;

    @FXML
    public void initialize() {
        userService = new UserService();
        usersList = FXCollections.observableArrayList();
        pendingUsersList = FXCollections.observableArrayList();

        setupTableColumns();
        setupPendingTableColumns();
        loadAllUsers();
        loadPendingUsers();
        updateStatistics();

        // Set up button actions
        usersButton.setOnAction(e -> showUsersView());
        pendingApprovalsButton.setOnAction(e -> showPendingApprovalsView());
        statisticsButton.setOnAction(e -> showStatisticsView());
        logoutButton.setOnAction(e -> handleLogout());
        searchButton.setOnAction(e -> handleSearch());
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupPendingTableColumns() {
        pendingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pendingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        pendingRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        pendingActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");

            {
                approveButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleApproveUser(user);
                });

                rejectButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleRejectUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, approveButton, rejectButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadAllUsers() {
        List<User> users = userService.getAll();
        usersList.setAll(users);
        usersTable.setItems(usersList);
    }

    private void loadPendingUsers() {
        List<User> pendingUsers = userService.getPendingUsers();
        pendingUsersList.setAll(pendingUsers);
        pendingTable.setItems(pendingUsersList);
    }

    private void updateStatistics() {
        totalUsersLabel.setText(String.valueOf(userService.getTotalUsersCount()));
        pendingApprovalsLabel.setText(String.valueOf(userService.getPendingUsersCount()));
        activeUsersLabel.setText(String.valueOf(userService.getActiveUsersCount()));
    }

    private void showUsersView() {
        usersView.setVisible(true);
        pendingApprovalsView.setVisible(false);
        statisticsView.setVisible(false);
    }

    private void showPendingApprovalsView() {
        usersView.setVisible(false);
        pendingApprovalsView.setVisible(true);
        statisticsView.setVisible(false);
        loadPendingUsers();
    }

    private void showStatisticsView() {
        usersView.setVisible(false);
        pendingApprovalsView.setVisible(false);
        statisticsView.setVisible(true);
        updateStatistics();
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadAllUsers();
            return;
        }

        List<User> filteredUsers = userService.searchUsers(searchTerm);
        usersList.setAll(filteredUsers);
    }

    private void handleEditUser(User user) {
        // TODO: Implement edit user functionality
        showAlert("Info", "Edit user functionality to be implemented", Alert.AlertType.INFORMATION);
    }

    private void handleDeleteUser(User user) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to delete this user?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            if (userService.deleteUser(user.getId())) {
                loadAllUsers();
                updateStatistics();
                showAlert("Success", "User deleted successfully", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to delete user", Alert.AlertType.ERROR);
            }
        }
    }

    private void handleApproveUser(User user) {
        if (userService.approveUser(user.getId())) {
            loadPendingUsers();
            loadAllUsers();
            updateStatistics();
            showAlert("Success", "User approved successfully", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to approve user", Alert.AlertType.ERROR);
        }
    }

    private void handleRejectUser(User user) {
        if (userService.rejectUser(user.getId())) {
            loadPendingUsers();
            updateStatistics();
            showAlert("Success", "User rejected successfully", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to reject user", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 