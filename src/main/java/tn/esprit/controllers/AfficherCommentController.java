package tn.esprit.controllers;

import tn.esprit.models.Commentaire_f;
import tn.esprit.models.Forum;
import tn.esprit.services.ServiceCommentaire_f;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import javafx.scene.control.ButtonType;

import java.sql.SQLException;

import javafx.util.Duration;


public class AfficherCommentController {

    @FXML
    private TableView<Commentaire_f> tableviewReponse;

    private final ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();

    // Dans la méthode initialize() de AfficherCommentController
    @FXML
    public void initialize() {
        // Définition des colonnes
        TableColumn<Commentaire_f, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Commentaire_f, String> texteColumn = new TableColumn<>("Texte");
        TableColumn<Commentaire_f, String> forumColumn = new TableColumn<>("Forum");
        TableColumn<Commentaire_f, Void> actionColumn = new TableColumn<>("Actions");

        // Association des colonnes avec les propriétés de l'entité
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_c().toString()));
        texteColumn.setCellValueFactory(new PropertyValueFactory<>("texte_c"));
        forumColumn.setCellValueFactory(cellData -> {
            Forum forum = cellData.getValue().getForum();
            return new SimpleStringProperty(forum != null ? forum.getTitre_f() : "N/A");
        });

        // Ajout des colonnes au TableView
        tableviewReponse.getColumns().addAll(dateColumn, texteColumn, forumColumn, actionColumn);

        // Style des colonnes
        dateColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        texteColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        forumColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        actionColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        // Ajout des boutons Modifier/Supprimer
        actionColumn.setCellFactory(param -> new TableCellWithButtons());

        // Style du TableView
        tableviewReponse.setStyle("-fx-background-color: #f4f4f4; -fx-font-family: 'Arial';");

        // Personnalisation de l'en-tête du TableView
        URL cssURL = getClass().getResource("styles/table-view.css");
        System.out.println("CSS URL: " + cssURL); // Print the URL

        // Chargement des commentaires
        loadCommentaires();

        // Ajouter une classe CSS pour l'animation
        tableviewReponse.getStyleClass().add("fade-in");

        // Animation pour le TableView
        FadeTransition ft = new FadeTransition(Duration.millis(1000), tableviewReponse);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            tableviewReponse.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page.");
        }
    }

    private void loadCommentaires() {
        System.out.println("Loading comments...");

        List<Commentaire_f> commentaires = serviceCommentaire.getAll();

        if (commentaires == null || commentaires.isEmpty()) {
            System.out.println("No comments fetched from service");
            return;
        }

        ObservableList<Commentaire_f> observableList = FXCollections.observableArrayList(commentaires);
        tableviewReponse.setItems(observableList);

        System.out.println("Comments loaded: " + observableList.size());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void pdf(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                float yPosition = 750;
                float margin = 50;
                float rowHeight = 25;
                float tableWidth = 500;
                float[] columnWidths = {100f, 250f, 150f};

                // Titre du PDF
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Liste des Commentaires");
                contentStream.endText();
                yPosition -= 50; // Plus d'espace après le titre

                // Fond gris pour l'en-tête
                contentStream.setNonStrokingColor(220, 220, 220);
                contentStream.addRect(margin, yPosition, tableWidth, rowHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0); // Noir pour le texte

                // En-tête du tableau
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 5, yPosition + 7);
                contentStream.showText("Date");
                contentStream.newLineAtOffset(columnWidths[0] + 20, 0); // Décalage du texte à droite
                contentStream.showText("Texte");
                contentStream.newLineAtOffset(columnWidths[1] + 20, 0); // Décalage du texte à droite
                contentStream.showText("Forum");
                contentStream.endText();
                yPosition -= rowHeight;

                // Récupérer les commentaires
                ObservableList<Commentaire_f> commentaires = tableviewReponse.getItems();

                if (commentaires.isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Aucun commentaire disponible.");
                    contentStream.endText();
                } else {
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    boolean alternateColor = false;

                    for (Commentaire_f commentaire : commentaires) {
                        if (commentaire != null) {
                            // Fond alterné pour la lisibilité
                            if (alternateColor) {
                                contentStream.setNonStrokingColor(245, 245, 245);
                                contentStream.addRect(margin, yPosition, tableWidth, rowHeight);
                                contentStream.fill();
                                contentStream.setNonStrokingColor(0, 0, 0);
                            }
                            alternateColor = !alternateColor;

                            // Texte dans le tableau
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin + 5, yPosition + 7);
                            contentStream.showText(commentaire.getDate_c().toString());
                            contentStream.newLineAtOffset(columnWidths[0] + 20, 0); // Texte un peu plus à droite
                            contentStream.showText(commentaire.getTexte_c());
                            contentStream.newLineAtOffset(columnWidths[1] + 20, 0); // Texte un peu plus à droite
                            contentStream.showText(commentaire.getForum().getTitre_f());
                            contentStream.endText();

                            // Lignes séparatrices
                            contentStream.moveTo(margin, yPosition);
                            contentStream.lineTo(margin + tableWidth, yPosition);
                            contentStream.stroke();

                            // Descendre d'une ligne
                            yPosition -= rowHeight;

                            // Saut de page si nécessaire
                            if (yPosition < 100) {
                                contentStream.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                contentStream = new PDPageContentStream(document, page);
                                yPosition = 750;
                            }
                        }
                    }
                }

                // Fermer et sauvegarder
                contentStream.close();
                document.save(file);
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "PDF généré avec succès !");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la génération du PDF.");
            }
        }
    }

    private class TableCellWithButtons extends TableCell<Commentaire_f, Void> {
        private final Button deleteButton = new Button("Supprimer");
        private final Button modifyButton = new Button("Modifier");

        TableCellWithButtons() {
            deleteButton.setOnAction(event -> {
                Commentaire_f commentaire = getTableView().getItems().get(getIndex());
                deleteCommentaire(commentaire);
            });

            modifyButton.setOnAction(event -> {
                Commentaire_f commentaire = getTableView().getItems().get(getIndex());
                modifyCommentaire(commentaire);
            });

            // Style des boutons
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            modifyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(5, deleteButton, modifyButton);
                buttons.setAlignment(Pos.CENTER);
                setGraphic(buttons);
            }
        }
    }

    private void deleteCommentaire(Commentaire_f commentaire) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression du commentaire");
        alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

        // Vérification si l'utilisateur confirme la suppression
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Suppression du commentaire de la base de données
                    serviceCommentaire.supprimer(commentaire.getId());

                    // Suppression du commentaire de la liste affichée
                    tableviewReponse.getItems().remove(commentaire);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire supprimé avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le commentaire.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void modifyCommentaire(Commentaire_f commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommentaire.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la fenêtre de modification
            ModifierCommentaireController modifierController = loader.getController();
            modifierController.setData(commentaire); // Envoyer le commentaire à modifier

            // Changer la scène
            tableviewReponse.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de modification.");
        }
    }
}