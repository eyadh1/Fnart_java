package tn.esprit.controllers;

import tn.esprit.models.Commentaire_f;
import tn.esprit.services.ServiceCommentaire_f;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.List;
import java.sql.SQLException;

public class AfficherCommentaireContainersController {

    @FXML
    private VBox commentContainerVBox;

    private final ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();

    @FXML
    public void initialize() {
        loadCommentaires();
    }

    private void loadCommentaires() {
        List<Commentaire_f> commentaires = serviceCommentaire.getAll();

        if (commentaires != null && !commentaires.isEmpty()) {
            Platform.runLater(() -> {  // Utiliser Platform.runLater pour les mises à jour de l'UI
                commentaires.forEach(commentaire -> {
                    VBox commentContainer = createCommentContainer(commentaire);
                    commentContainerVBox.getChildren().add(commentContainer);
                });
            });
        } else {
            System.out.println("No comments found.");
        }
    }

    private VBox createCommentContainer(Commentaire_f commentaire) {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px; -fx-border-radius: 5px;");
        container.setSpacing(5);
        container.setPadding(new Insets(10));
        HBox.setHgrow(container, Priority.ALWAYS);

        Label forumLabel = new Label("Forum: " + commentaire.getForum().getTitre_f());
        forumLabel.setFont(Font.font("Arial", 14));

        Label dateLabel = new Label("Date: " + commentaire.getDate_c().toString());
        dateLabel.setFont(Font.font("Arial", 12));

        Label textLabel = new Label("Texte: " + commentaire.getTexte_c());
        textLabel.setFont(Font.font("Arial", 12));
        textLabel.setWrapText(true);  // Permettre le retour à la ligne automatique
        textLabel.prefWidthProperty().bind(commentContainerVBox.widthProperty().subtract(50));  // Ajuster la largeur

        Button modifierButton = new Button("Modifier");
        modifierButton.setOnAction(event -> modifierCommentaire(commentaire));
        modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 3px;");

        container.getChildren().addAll(forumLabel, dateLabel, textLabel, modifierButton);
        return container;
    }

    private void modifierCommentaire(Commentaire_f commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommentaire.fxml"));
            Parent root = loader.load();

            ModifierCommentaireController modifierController = loader.getController();
            modifierController.setData(commentaire);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Commentaire");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}