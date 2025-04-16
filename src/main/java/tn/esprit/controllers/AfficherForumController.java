package tn.esprit.controllers;

import tn.esprit.models.Forum;
import tn.esprit.services.ServiceForum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AfficherForumController {

    @FXML
    private AnchorPane MainAnchorPaneBaladity;

    @FXML
    private TextField RechercherActualiteAdmin;

    @FXML
    private Button buttonreturnA;

    @FXML
    private GridPane gridAdmin;

    @FXML
    private ScrollPane scrollAdmin;

    @FXML
    private Button sortActualiteAdmin;

    private ServiceForum serviceForum = new ServiceForum();

    @FXML
    void initialize() {
        afficherForumList();
    }

    private void afficherForumList() {
        try {
            List<Forum> forumSet = serviceForum.getAll();
            List<Forum> forumList = new ArrayList<>(forumSet);

            gridAdmin.getChildren().clear();
            gridAdmin.getColumnConstraints().clear();
            gridAdmin.getRowConstraints().clear();

            int column = 0;
            int row = 0;
            int columnsPerRow = 3;

            for (Forum forum : forumList) {
                Text forumTitle = new Text(forum.getTitre_f());
                forumTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                Text forumDescription = new Text(forum.getDescription_f());
                forumDescription.setStyle("-fx-font-size: 14px;");

                Text forumDate = new Text("Published on: " + forum.getDate_f());
                forumDate.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

                Text forumCategory = new Text("Category: " + forum.getCategorie_f());
                forumCategory.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; color: #5b5b5b;");

                ImageView imageView = new ImageView();
                if (forum.getImage_f() != null && !forum.getImage_f().isEmpty()) {
                    try {
                        Image image = new Image("file:" + forum.getImage_f(), 150, 150, true, true);
                        imageView.setImage(image);
                    } catch (Exception e) {
                        System.out.println("Error loading image: " + e.getMessage());
                    }
                }
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

                VBox textContainer = new VBox(5);
                textContainer.getChildren().addAll(forumTitle, forumDescription, forumDate, forumCategory);

                HBox forumBox = new HBox(15);
                forumBox.getChildren().addAll(imageView, textContainer);
                forumBox.setStyle("-fx-padding: 10px; -fx-border-color: #ccc; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Button modifyButton = new Button("Modify");
                modifyButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                modifyButton.setOnAction(event -> modifyForum(forum));

                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> deleteForum(forum));

                Button commentButton = new Button("Comment");
                commentButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                commentButton.setOnAction(event -> openCommentPage(forum));

                VBox forumContainer = new VBox(10);
                forumContainer.getChildren().addAll(forumBox, modifyButton, deleteButton,commentButton);
                forumContainer.setStyle("-fx-padding: 15px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

                gridAdmin.add(forumContainer, column, row);
                column++;

                if (column == columnsPerRow) {
                    column = 0;
                    row++;
                }
            }

            gridAdmin.setVgap(20);
            gridAdmin.setHgap(20);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load forum data.");
            e.printStackTrace();
        }
    }

    private void openCommentPage(Forum forum) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterCommentaires.fxml")); // Adjust the path if necessary

            // Set the new scene
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


    private void deleteForum(Forum forum) {
        try {
            serviceForum.supprimer(forum.getId());
            afficherForumList();
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
        // Get the search text from the TextField
        String searchText = RechercherActualiteAdmin.getText().toLowerCase();

        try {
            List<Forum> forumSet = serviceForum.getAll();
            List<Forum> forumList = new ArrayList<>(forumSet);

            // Filter the forum list based on the title (or other fields if needed)
            List<Forum> filteredList = forumList.stream()
                    .filter(forum -> forum.getTitre_f().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            // Clear the existing grid and display the filtered list
            gridAdmin.getChildren().clear();
            afficherForumList(filteredList); // Method to display forums
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load forum data.");
            e.printStackTrace();
        }
    }



    // Method to sort forums by date
    @FXML
    private void sortActualiteAdmin(ActionEvent event) {
        try {
            List<Forum> forumSet = serviceForum.getAll();
            List<Forum> forumList = new ArrayList<>(forumSet);

            // Sort by date: Convert java.util.Date to LocalDate
            forumList.sort(Comparator.comparing(forum -> {
                String forumDateStr = String.valueOf(forum.getDate_f());  // assuming forum.getDate_f() returns a String
                if (forumDateStr != null && !forumDateStr.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(forumDateStr, formatter);
                    return localDate;
                } else {
                    return LocalDate.MIN;  // Default value if the date is null or empty
                }
            }));



            // Clear the grid and re-populate it with the sorted list
            gridAdmin.getChildren().clear();
            afficherForumList(forumList); // Method to display sorted forums
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load forum data.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to sort forums by date.");
            e.printStackTrace();
        }
    }


    private void afficherForumList(List<Forum> forumList) {
        int column = 0;
        int row = 0;
        int columnsPerRow = 3;

        for (Forum forum : forumList) {
            Text forumTitle = new Text(forum.getTitre_f());
            forumTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Text forumDescription = new Text(forum.getDescription_f());
            forumDescription.setStyle("-fx-font-size: 14px;");

            Text forumDate = new Text("Published on: " + forum.getDate_f());
            forumDate.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

            Text forumCategory = new Text("Category: " + forum.getCategorie_f());
            forumCategory.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; color: #5b5b5b;");

            ImageView imageView = new ImageView();
            if (forum.getImage_f() != null && !forum.getImage_f().isEmpty()) {
                try {
                    Image image = new Image("file:" + forum.getImage_f(), 150, 150, true, true);
                    imageView.setImage(image);
                } catch (Exception e) {
                    System.out.println("Error loading image: " + e.getMessage());
                }
            }
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            VBox textContainer = new VBox(5);
            textContainer.getChildren().addAll(forumTitle, forumDescription, forumDate, forumCategory);

            HBox forumBox = new HBox(15);
            forumBox.getChildren().addAll(imageView, textContainer);
            forumBox.setStyle("-fx-padding: 10px; -fx-border-color: #ccc; -fx-border-radius: 10px; -fx-background-radius: 10px;");

            Button modifyButton = new Button("Modify");
            modifyButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
            modifyButton.setOnAction(event -> modifyForum(forum));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> deleteForum(forum));

            VBox forumContainer = new VBox(10);
            forumContainer.getChildren().addAll(forumBox, modifyButton, deleteButton);
            forumContainer.setStyle("-fx-padding: 15px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

            gridAdmin.add(forumContainer, column, row);
            column++;

            if (column == columnsPerRow) {
                column = 0;
                row++;
            }
        }

        gridAdmin.setVgap(20);
        gridAdmin.setHgap(20);
    }

    @FXML
    void buttonreturnA(ActionEvent event) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterForum.fxml")); // Adjust the path if necessary

            // Set the new scene
            Stage stage = (Stage) buttonreturnA.getScene().getWindow();
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


    public void chat(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/ChatBotTache.fxml")); // Adjust the path if necessary

            // Set the new scene
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
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/stat.fxml")); // Adjust the path if necessary

            // Set the new scene
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
}
