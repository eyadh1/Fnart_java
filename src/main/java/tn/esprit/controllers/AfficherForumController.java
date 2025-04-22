package tn.esprit.controllers;

import tn.esprit.models.Forum;
import tn.esprit.services.ServiceForum;
import tn.esprit.models.ElasticScroll;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AfficherForumController {

    @FXML
    private AnchorPane MainAnchorPaneBaladity;

    @FXML
    private TextField RechercherActualiteAdmin;

    @FXML
    private Button buttonreturnA;

    @FXML
    private TableView<Forum> forumTableView;

    @FXML
    private TableColumn<Forum, String> titleColumn;

    @FXML
    private TableColumn<Forum, String> dateColumn;

    @FXML
    private TableColumn<Forum, String> categoryColumn;

    @FXML
    private TableColumn<Forum, String> descriptionColumn;

    @FXML
    private TableColumn<Forum, Void> actionsColumn; // Column for edit/delete buttons

    @FXML
    private ScrollPane scrollAdmin;

    @FXML
    private Button sortActualiteAdmin;

    @FXML
    private ComboBox<String> categoryFilterComboBox;  // Added ComboBox

    private ServiceForum serviceForum = new ServiceForum();

    private ObservableList<Forum> forumList = FXCollections.observableArrayList();
    private ObservableList<Forum> masterForumList = FXCollections.observableArrayList(); // Keep a copy of the original data

    @FXML
    void initialize() {
        ElasticScroll.applyElasticScrolling(scrollAdmin);
        setupTableColumns();
        loadForumData();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre_f"));
        dateColumn.setCellValueFactory(cellData -> {
            String dateStr = String.valueOf(cellData.getValue().getDate_f());
            LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categorie_f"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description_f"));

        // Actions column (Edit/Delete)
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.setOnAction(event -> {
                    Forum forum = getTableRow().getItem();
                    if (forum != null) {
                        modifyForum(forum);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Forum forum = getTableRow().getItem();
                    if (forum != null) {
                        deleteForum(forum);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadForumData() {
        try {
            Set<Forum> forumSet = serviceForum.getAll();
            forumList.clear();
            forumList.addAll(forumSet);
            masterForumList.clear();  // Update the master list as well.
            masterForumList.addAll(forumSet);

            // Populate the category filter ComboBox:
            Set<String> categories = forumSet.stream()
                    .map(Forum::getCategorie_f)
                    .collect(Collectors.toSet());
            List<String> categoryList = new ArrayList<>(categories);
            categoryList.add(0, "Toutes les catégories");  // Add "All Categories" option
            categoryFilterComboBox.setItems(FXCollections.observableArrayList(categoryList));
            categoryFilterComboBox.setValue("Toutes les catégories");  // Set default selection

            forumTableView.setItems(forumList);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load forum data.");
            e.printStackTrace();
        }
    }

    private void deleteForum(Forum forum) {
        try {
            serviceForum.supprimer(forum.getId());
            loadForumData(); // Refresh table
            showAlert("Success", "Forum deleted successfully.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete the forum.");
            e.printStackTrace();
        }
    }

    private void modifyForum(Forum forum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForum.fxml"));
            Parent root = loader.load();

            ModifierForumController controller = loader.getController();
            controller.setServiceForum(serviceForum);
            controller.setData(forum);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open modify forum window.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void RechercherActualiteAdmin(ActionEvent event) {
        String searchText = RechercherActualiteAdmin.getText().toLowerCase();

        List<Forum> filteredList = masterForumList.stream()
                .filter(forum -> forum.getTitre_f().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        forumList.clear();  // Clear the observable list
        forumList.addAll(filteredList); // Add filtered items
        forumTableView.setItems(forumList); // Update the table
    }

    @FXML
    private void sortActualiteAdmin(ActionEvent event) {
        List<Forum> sortedList = new ArrayList<>(forumList);  // Sort the currently displayed list

        sortedList.sort(Comparator.comparing(forum -> {
            String forumDateStr = String.valueOf(forum.getDate_f());
            if (forumDateStr != null && !forumDateStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(forumDateStr, formatter);
                return localDate;
            } else {
                return LocalDate.MIN;
            }
        }));

        forumList.clear();
        forumList.addAll(sortedList);
        forumTableView.setItems(forumList);
    }

    @FXML
    void filterByCategory(ActionEvent event) {
        String selectedCategory = categoryFilterComboBox.getValue();

        if (selectedCategory == null || selectedCategory.equals("Toutes les catégories")) {
            // Show all forums
            forumList.clear();
            forumList.addAll(masterForumList);
        } else {
            // Filter by selected category
            List<Forum> filteredList = masterForumList.stream()
                    .filter(forum -> forum.getCategorie_f().equals(selectedCategory))
                    .collect(Collectors.toList());
            forumList.clear();
            forumList.addAll(filteredList);
        }

        forumTableView.setItems(forumList);
    }

    public void chat(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ChatBotTache.fxml"));
            Stage stage = (Stage) scrollAdmin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Erreur lors du chargement de la vue.");
            alert.setTitle("Erreur");
            alert.show();
        }
    }

    public void stat(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/stat.fxml"));
            Stage stage = (Stage) scrollAdmin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Erreur lors du chargement de la vue.");
            alert.setTitle("Erreur");
            alert.show();
        }
    }

    public void loadForumList() {
        loadForumData();
    }
}