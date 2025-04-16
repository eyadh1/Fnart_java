package tn.esprit.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import tn.esprit.models.CommentaireF;
import tn.esprit.models.Forum;
import tn.esprit.services.ServiceCommentaire_f;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherCommentController {

    @FXML
    private TableView<CommentaireF> tableviewReponse;

    @FXML
    private Button gotoajoutercomment;

    @FXML
    private Button recccccccccccccccccccccccc;

    @FXML
    private Button pdf;

    private final ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();

    @FXML
    public void initialize() {
        // Define columns
        TableColumn<CommentaireF, String> dateColumn = new TableColumn<>("Date");
        TableColumn<CommentaireF, String> texteColumn = new TableColumn<>("Texte");
        TableColumn<CommentaireF, String> forumColumn = new TableColumn<>("Forum");
        TableColumn<CommentaireF, Void> actionColumn = new TableColumn<>("Actions");

        // Associate columns with entity properties
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate_c() != null) {
                return new SimpleStringProperty(cellData.getValue().getDate_c().toString());
            }
            return new SimpleStringProperty("N/A");
        });
        texteColumn.setCellValueFactory(new PropertyValueFactory<>("texte_c"));
        forumColumn.setCellValueFactory(cellData -> {
            Forum forum = cellData.getValue().getForum();
            return new SimpleStringProperty(forum != null ? forum.getTitre_f() : "N/A");
        });

        // Add columns to TableView
        tableviewReponse.getColumns().addAll(dateColumn, texteColumn, forumColumn, actionColumn);

        // Add Modify/Delete buttons
        actionColumn.setCellFactory(param -> new TableCellWithButtons());

        // Load comments
        loadCommentaires();
    }

    @FXML
    public void gotoajoutercomment(ActionEvent actionEvent) {
        try {
            // Load the FXML file using the correct path
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterCommentaires.fxml"));

            // Set the new scene
            Stage stage = (Stage) gotoajoutercomment.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la vue.");
        }
    }

    @FXML
    public void recccccccccccccccccccccccc(ActionEvent actionEvent) {
        try {
            // Load the FXML file using the correct path
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherForum.fxml"));

            // Set the new scene
            Stage stage = (Stage) recccccccccccccccccccccccc.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la vue.");
        }
    }

    private void loadCommentaires() {
        System.out.println("Loading comments...");

        List<CommentaireF> commentaires = serviceCommentaire.getAll();

        if (commentaires == null || commentaires.isEmpty()) {
            System.out.println("No comments fetched from service");
            return;
        }

        ObservableList<CommentaireF> observableList = FXCollections.observableArrayList(commentaires);
        tableviewReponse.setItems(observableList);

        System.out.println("Comments loaded: " + observableList.size());
    }

    @FXML
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

                // PDF Title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Liste des Commentaires");
                contentStream.endText();
                yPosition -= 50; // More space after title

                // Gray background for header
                contentStream.setNonStrokingColor(220, 220, 220);
                contentStream.addRect(margin, yPosition, tableWidth, rowHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0); // Black for text

                // Table header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 5, yPosition + 7);
                contentStream.showText("Date");
                contentStream.newLineAtOffset(columnWidths[0] + 20, 0);
                contentStream.showText("Texte");
                contentStream.newLineAtOffset(columnWidths[1] + 20, 0);
                contentStream.showText("Forum");
                contentStream.endText();
                yPosition -= rowHeight;

                // Get comments
                ObservableList<CommentaireF> commentaires = tableviewReponse.getItems();

                if (commentaires.isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Aucun commentaire disponible.");
                    contentStream.endText();
                } else {
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    boolean alternateColor = false;

                    for (CommentaireF commentaire : commentaires) {
                        if (commentaire != null) {
                            // Alternate background for readability
                            if (alternateColor) {
                                contentStream.setNonStrokingColor(245, 245, 245);
                                contentStream.addRect(margin, yPosition, tableWidth, rowHeight);
                                contentStream.fill();
                                contentStream.setNonStrokingColor(0, 0, 0);
                            }
                            alternateColor = !alternateColor;

                            // Text in table
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin + 5, yPosition + 7);
                            contentStream.showText(commentaire.getDate_c().toString());
                            contentStream.newLineAtOffset(columnWidths[0] + 20, 0);
                            contentStream.showText(commentaire.getTexte_c());
                            contentStream.newLineAtOffset(columnWidths[1] + 20, 0);
                            contentStream.showText(commentaire.getForum().getTitre_f());
                            contentStream.endText();

                            // Separator lines
                            contentStream.moveTo(margin, yPosition);
                            contentStream.lineTo(margin + tableWidth, yPosition);
                            contentStream.stroke();

                            // Move down one line
                            yPosition -= rowHeight;

                            // Page break if needed
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

                // Close and save
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private class TableCellWithButtons extends TableCell<CommentaireF, Void> {
        private final Button deleteButton = new Button("Supprimer");
        private final Button modifyButton = new Button("Modifier");
        private final HBox pane = new HBox(5);

        TableCellWithButtons() {
            pane.getChildren().addAll(deleteButton, modifyButton);

            deleteButton.setOnAction(event -> {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    CommentaireF commentaire = (CommentaireF) getTableRow().getItem();
                    deleteCommentaire(commentaire);
                }
            });

            modifyButton.setOnAction(event -> {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    CommentaireF commentaire = (CommentaireF) getTableRow().getItem();
                    modifyCommentaire(commentaire);
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(pane);
            }
        }
    }

    private void deleteCommentaire(CommentaireF commentaire) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression du commentaire");
        alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

        // Check if user confirms deletion
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete comment from database
                    serviceCommentaire.supprimer(commentaire.getId());

                    // Remove comment from displayed list
                    tableviewReponse.getItems().remove(commentaire);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire supprimé avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le commentaire.");
                }
            }
        });
    }

    private void modifyCommentaire(CommentaireF commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommentaire.fxml"));
            Parent root = loader.load();

            // Get the controller for the modification window
            ModifierCommentaireController modifierController = loader.getController();
            modifierController.setData(commentaire); // Send the comment to modify

            // Change scene
            Stage stage = (Stage) tableviewReponse.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de modification.");
        }
    }
}