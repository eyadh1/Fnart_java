package tn.esprit.controllers;

import tn.esprit.models.Forum;
import tn.esprit.services.ServiceForum;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

public class ForumClientController {

    @FXML
    private VBox forumContainer; // Container to hold forum cards

    private ServiceForum serviceForum = new ServiceForum();

    @FXML
    public void initialize() {
        loadForums();
    }

    private void loadForums() {
        try {
            Set<Forum> forums = serviceForum.getAll();
            for (Forum forum : forums) {
                forumContainer.getChildren().add(createForumCard(forum));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert)
        }
    }

    private HBox createForumCard(Forum forum) {
        HBox card = new HBox();
        card.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
        card.setPadding(new Insets(10));
        card.setSpacing(10);

        ImageView imageView = new ImageView();
        try {
            String imagePath = forum.getImage_f();
            System.out.println("Image path for forum " + forum.getTitre_f() + ": " + imagePath);
            if (imagePath != null && !imagePath.isEmpty()) {

                Image image = new Image("file:" + imagePath);
                imageView.setImage(image);
            } else {

                Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/default-forum.png")));
                imageView.setImage(defaultImage);
            }

        } catch (Exception e) {
            // Handle image loading errors gracefully
            System.err.println("Failed to load forum image: " + e.getMessage());
            e.printStackTrace();
            Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/default-forum.png")));
            imageView.setImage(defaultImage);
        }
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        // Add click event to the image view
        imageView.setOnMouseClicked(event -> {
            showImagePopup(imageView.getImage());
        });


        VBox details = new VBox();
        details.setSpacing(5);

        Label titleLabel = new Label("Title: " + forum.getTitre_f());
        titleLabel.setFont(new Font(16));

        LocalDate localDate = LocalDate.parse(String.valueOf(forum.getDate_f()), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Label dateLabel = new Label("Date: " + localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Label categoryLabel = new Label("Category: " + forum.getCategorie_f());
        Label descriptionLabel = new Label("Description: " + forum.getDescription_f());

        details.getChildren().addAll(titleLabel, dateLabel, categoryLabel, descriptionLabel);

        card.getChildren().addAll(imageView, details);
        return card;
    }


    private void showImagePopup(Image image) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
        popupStage.setTitle("Image Viewer");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600); // You can adjust the size as needed
        imageView.setFitHeight(600);

        StackPane layout = new StackPane(imageView);
        layout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Optional: semi-transparent background

        Scene popupScene = new Scene(layout, 600, 600);
        popupStage.setScene(popupScene);

        // Close the popup when clicking outside the image (on the StackPane background).
        layout.setOnMouseClicked(event -> {
            popupStage.close();
        });

        popupStage.showAndWait(); // Show the popup and wait for it to be closed.
    }
}