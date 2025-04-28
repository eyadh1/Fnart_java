package tn.esprit.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.models.User;

public class UserCard extends HBox {
    private final User user;

    public UserCard(User user) {
        this.user = user;
        setSpacing(15);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.07), 5, 0, 0, 1);");

        // Profile image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            imageView.setImage(new Image(user.getProfilePicture(), true));
        } else {
            imageView.setImage(new Image(getClass().getResource("/assets/default-profile.png").toExternalForm()));
        }
        imageView.setStyle("-fx-background-radius: 50%;");

        // User info
        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(user.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        Label subtitleLabel = new Label(user.getEmail());
        subtitleLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");

        infoBox.getChildren().addAll(nameLabel, subtitleLabel);

        // Action button
        Button actionButton = new Button("Voir le profil");
        actionButton.setStyle("-fx-background-color: #e7f3ff; -fx-text-fill: #1877f2; -fx-background-radius: 8;");
        // You can add an event handler here

        getChildren().addAll(imageView, infoBox, actionButton);
    }

    public User getUser() {
        return user;
    }
} 