package tn.esprit.controllers;

import tn.esprit.models.Forum;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ForumItemController {
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label categoryLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView forumImageView;

    public void setData(Forum forum) {
        titleLabel.setText(forum.getTitre_f());
        dateLabel.setText(forum.getFormattedDate());
        categoryLabel.setText(forum.getCategorie_f());
        descriptionLabel.setText(forum.getDescription_f());

        if (forum.getImage_f() != null && !forum.getImage_f().isEmpty()) {
            try {
                Image image = new Image("file:" + forum.getImage_f());
                forumImageView.setImage(image);
            } catch (Exception e) {
                // Si l'image ne peut pas être chargée, utiliser une image par défaut
                forumImageView.setImage(new Image(getClass().getResourceAsStream("/assets/default-forum.png")));
            }
        }
    }
}