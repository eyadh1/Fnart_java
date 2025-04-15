package tn.esprit.controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;
import javafx.util.Callback;

import javafx.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class listartwork {

    @FXML
    private TableView<artwork> tableView;

    @FXML
    private TableColumn<artwork, String> titreTableColumn;

    @FXML
    private TableColumn<artwork, String> artistenomTableColumn;

    @FXML
    private TableColumn<artwork, String> descriptionTableColumn;

    @FXML
    private TableColumn<artwork, Integer> prixTableColumn;

    @FXML
    private TableColumn<artwork, String> statusTableColumn;

    @FXML
    private TableColumn<artwork, String> imageTableCollumn;

    @FXML
    private TableColumn<artwork, Void> commanderTableColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> triComboBox;

    @FXML
    private TextField minPrixField;

    @FXML
    private TextField maxPrixField;

    @FXML
    private PieChart statisticsChart;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnVisualiser;

    private ObservableList<artwork> artworkList;
    private FilteredList<artwork> filteredData;

    serviceartwork service = new serviceartwork();

    @FXML
    public void initialize() {
        // Associer les colonnes aux attributs de l'entité
        titreTableColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        artistenomTableColumn.setCellValueFactory(new PropertyValueFactory<>("artistenom"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixTableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageTableCollumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Initialiser la ComboBox de tri
        triComboBox.getItems().addAll("Titre", "Prix croissant", "Prix décroissant", "Artiste");
        triComboBox.setOnAction(e -> handleSort());

        // Ajouter le bouton Commander dans chaque ligne
        addCommanderButton();

        // Style du bouton visualiser
        btnVisualiser.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
        btnVisualiser.setTooltip(new Tooltip("Voir toutes les commandes"));

        // Récupérer et afficher les données
        artworkList = FXCollections.observableArrayList(service.getAll());
        
        // Configurer la recherche dynamique
        setupSearch();
        
        // Initialiser les statistiques
        updateStatistics();

        // Configure image column to display images
        imageTableCollumn.setCellFactory(column -> {
            return new TableCell<artwork, String>() {
                private final ImageView imageView = new ImageView();
                
                {
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    imageView.setPreserveRatio(true);
                }
                
                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);
                    
                    if (empty || imagePath == null) {
                        setGraphic(null);
                    } else {
                        try {
                            String fullPath = "src/main/resources/uploads/" + imagePath;
                            File file = new File(fullPath);
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                imageView.setImage(image);
                                setGraphic(imageView);
                            } else {
                                setGraphic(null);
                                setText("Image non trouvée");
                            }
                        } catch (Exception e) {
                            setGraphic(null);
                            setText("Erreur");
                        }
                    }
                }
            };
        });
    }

    private void setupSearch() {
        // Initialiser la liste filtrée
        filteredData = new FilteredList<>(artworkList, p -> true);

        // Configurer le listener pour la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(artwork -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return artwork.getTitre().toLowerCase().contains(lowerCaseFilter)
                        || artwork.getArtistenom().toLowerCase().contains(lowerCaseFilter)
                        || artwork.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Lier la liste filtrée à la TableView
        SortedList<artwork> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    @FXML
    private void filterByPrice() {
        try {
            double minPrix = minPrixField.getText().isEmpty() ? 0 : Double.parseDouble(minPrixField.getText());
            double maxPrix = maxPrixField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPrixField.getText());

            filteredData.setPredicate(artwork -> {
                double prix = artwork.getPrix();
                return prix >= minPrix && prix <= maxPrix;
            });
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer des valeurs numériques valides pour les prix.");
        }
    }

    private void handleSort() {
        String selectedSort = triComboBox.getValue();
        if (selectedSort != null) {
            switch (selectedSort) {
                case "Titre":
                    tableView.getSortOrder().clear();
                    titreTableColumn.setSortType(TableColumn.SortType.ASCENDING);
                    tableView.getSortOrder().add(titreTableColumn);
                    break;
                case "Prix croissant":
                    tableView.getSortOrder().clear();
                    prixTableColumn.setSortType(TableColumn.SortType.ASCENDING);
                    tableView.getSortOrder().add(prixTableColumn);
                    break;
                case "Prix décroissant":
                    tableView.getSortOrder().clear();
                    prixTableColumn.setSortType(TableColumn.SortType.DESCENDING);
                    tableView.getSortOrder().add(prixTableColumn);
                    break;
                case "Artiste":
                    tableView.getSortOrder().clear();
                    artistenomTableColumn.setSortType(TableColumn.SortType.ASCENDING);
                    tableView.getSortOrder().add(artistenomTableColumn);
                    break;
            }
        }
    }

    @FXML
    private void exportToPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

            if (file != null) {
                // Create PDF document
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Add title
                document.add(new Paragraph("Liste des Œuvres d'Art"));
                document.add(new Paragraph("\n"));

                // Add artwork information
                for (artwork art : artworkList) {
                    document.add(new Paragraph("Titre: " + art.getTitre()));
                    document.add(new Paragraph("Artiste: " + art.getArtistenom()));
                    document.add(new Paragraph("Description: " + art.getDescription()));
                    document.add(new Paragraph("Prix: " + art.getPrix() + " €"));
                    document.add(new Paragraph("Status: " + art.getStatus()));
                    document.add(new Paragraph("\n"));
                }

                // Close document
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le PDF a été généré avec succès !");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    private void updateStatistics() {
        // Calculer les statistiques de prix
        Map<String, Long> priceRanges = artworkList.stream()
                .collect(Collectors.groupingBy(art -> {
                    double prix = art.getPrix();
                    if (prix < 100) return "< 100€";
                    else if (prix < 500) return "100€ - 500€";
                    else if (prix < 1000) return "500€ - 1000€";
                    else return "> 1000€";
                }, Collectors.counting()));

        // Mettre à jour le graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        priceRanges.forEach((range, count) -> 
            pieChartData.add(new PieChart.Data(range + " (" + count + ")", count))
        );
        
        statisticsChart.setData(pieChartData);
    }

    private void addCommanderButton() {
        Callback<TableColumn<artwork, Void>, TableCell<artwork, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<artwork, Void> call(final TableColumn<artwork, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Commander");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            artwork artwork = getTableView().getItems().get(getIndex());
                            commanderArtwork(artwork);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        commanderTableColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void visualiserToutesCommandes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcommande.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste de toutes les commandes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la liste des commandes : " + e.getMessage());
        }
    }

    private void commanderArtwork(artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutercommande.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer l'artwork
            ajoutercommandeController controller = loader.getController();
            controller.initData(artwork);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Commander - " + artwork.getTitre());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de commande : " + e.getMessage());
        }
    }

    @FXML
    private void addArtwork(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterartwork.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Artwork");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void btnupdate() {
        artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

        if (selectedArtwork != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierartwork.fxml"));
                Parent root = loader.load();

                modifierartworkController controller = loader.getController();
                controller.setArtwork(selectedArtwork);
                controller.setParentController(this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Œuvre - " + selectedArtwork.getTitre());
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface de modification : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une œuvre à modifier.");
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void deleteart(ActionEvent event) {
        artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

        if (selectedArtwork != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selectedArtwork.getTitre() + "'?");

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                try {
                    service.delete(selectedArtwork);
                    tableView.getItems().remove(selectedArtwork);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "L'œuvre a été supprimée avec succès.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une œuvre à supprimer.");
        }
    }

    public void refreshTable() {
        List<artwork> artworks = service.getAll();
        tableView.setItems(FXCollections.observableArrayList(artworks));
    }
}
