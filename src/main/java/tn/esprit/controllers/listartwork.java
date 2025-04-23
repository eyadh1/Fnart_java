package tn.esprit.controllers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
import tn.esprit.models.Artwork;
import tn.esprit.services.serviceartwork;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.URL;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import tn.esprit.models.Commande;
import tn.esprit.services.ServiceCommande;
import javafx.scene.text.Text;

public class listartwork {

    @FXML
    private TableView<Artwork> tableView;

    @FXML
    private TableColumn<Artwork, String> titreTableColumn;

    @FXML
    private TableColumn<Artwork, String> artistenomTableColumn;

    @FXML
    private TableColumn<Artwork, String> descriptionTableColumn;

    @FXML
    private TableColumn<Artwork, Integer> prixTableColumn;

    @FXML
    private TableColumn<Artwork, String> imageTableCollumn;

    @FXML
    private TableColumn<Artwork, Void> commanderTableColumn;

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

    @FXML
    private GridPane artworkGrid;

    private ObservableList<Artwork> artworkList;
    private FilteredList<Artwork> filteredData;

    serviceartwork service = new serviceartwork();

    @FXML
    public void initialize() {
        // Associer les colonnes aux attributs de l'entité
        titreTableColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        artistenomTableColumn.setCellValueFactory(new PropertyValueFactory<>("artistenom"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixTableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
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
            return new TableCell<Artwork, String>() {
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
                            String fullPath = "C:\\xampp\\htdocs" + imagePath;
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

        loadArtworks();
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
        SortedList<Artwork> sortedData = new SortedList<>(filteredData);
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
                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);

                    // Add title
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText("Liste des Œuvres d'Art");
                    contentStream.endText();

                    float margin = 50;
                    float yStart = 700;
                    float yPosition = yStart;
                    float rowHeight = 20;

                    // Headers
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Titre");
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText("Artiste");
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText("Prix");
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.endText();

                    yPosition -= rowHeight * 1.5;
                    contentStream.setFont(PDType1Font.HELVETICA, 12);

                    // Content
                    for (Artwork art : artworkList) {
                        if (yPosition < 50) {
                            contentStream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                            yPosition = yStart;
                        }

                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(truncateText(art.getTitre(), 25));
                        contentStream.newLineAtOffset(150, 0);
                        contentStream.showText(truncateText(art.getArtistenom(), 25));
                        contentStream.newLineAtOffset(150, 0);
                        contentStream.showText(art.getPrix() + " €");
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.endText();

                        yPosition -= rowHeight;
                    }
                    
                    contentStream.close();
                    document.save(file);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le PDF a été généré avec succès !");
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
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
        Callback<TableColumn<Artwork, Void>, TableCell<Artwork, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Artwork, Void> call(final TableColumn<Artwork, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Commander");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Artwork artwork = getTableView().getItems().get(getIndex());
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

    private void commanderArtwork(Artwork artwork) {
        try {
            System.out.println("Creating order form for artwork: " + artwork.getTitre());
            
            // Créer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Commander - " + artwork.getTitre());
            
            // Créer les composants
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: white;");
            
            Label titleLabel = new Label("Finaliser la commande");
            titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
            
            // Nom
            VBox nomBox = new VBox(5);
            Label nomLabel = new Label("Nom");
            nomLabel.setStyle("-fx-font-weight: bold;");
            TextField nomField = new TextField();
            nomField.setPromptText("Entrez votre nom");
            nomBox.getChildren().addAll(nomLabel, nomField);
            
            // Prénom
            VBox prenomBox = new VBox(5);
            Label prenomLabel = new Label("Prénom");
            prenomLabel.setStyle("-fx-font-weight: bold;");
            TextField prenomField = new TextField();
            prenomField.setPromptText("Entrez votre prénom");
            prenomBox.getChildren().addAll(prenomLabel, prenomField);
            
            // Adresse
            VBox adresseBox = new VBox(5);
            Label adresseLabel = new Label("Adresse de livraison");
            adresseLabel.setStyle("-fx-font-weight: bold;");
            TextField adresseField = new TextField();
            adresseField.setPromptText("Entrez votre adresse");
            adresseBox.getChildren().addAll(adresseLabel, adresseField);
            
            // Téléphone
            VBox telBox = new VBox(5);
            Label telLabel = new Label("Numéro de téléphone");
            telLabel.setStyle("-fx-font-weight: bold;");
            TextField telField = new TextField();
            telField.setPromptText("Entrez votre numéro");
            telBox.getChildren().addAll(telLabel, telField);
            
            // Email
            VBox emailBox = new VBox(5);
            Label emailLabel = new Label("Email");
            emailLabel.setStyle("-fx-font-weight: bold;");
            TextField emailField = new TextField();
            emailField.setPromptText("Entrez votre email");
            emailBox.getChildren().addAll(emailLabel, emailField);
            
            // Total
            Label totalLabel = new Label(String.format("Total: %.2f TND", artwork.getPrix()));
            totalLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            
            // Boutons
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            
            Button cancelButton = new Button("Annuler");
            cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            
            Button confirmButton = new Button("Confirmer");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            
            buttonBox.getChildren().addAll(cancelButton, confirmButton);
            
            // Ajouter tous les composants au root
            root.getChildren().addAll(
                titleLabel,
                nomBox,
                prenomBox,
                adresseBox,
                telBox,
                emailBox,
                totalLabel,
                buttonBox
            );
            
            // Actions des boutons
            cancelButton.setOnAction(e -> stage.close());
            
            confirmButton.setOnAction(e -> {
                if (validateFields(nomField, prenomField, adresseField, telField, emailField)) {
                    try {
                        // Créer la commande
                        Commande commande = new Commande();
                        commande.setNom(nomField.getText().trim() + " " + prenomField.getText().trim());
                        commande.setAdress(adresseField.getText().trim());
                        commande.setTelephone(telField.getText().trim());
                        commande.setEmail(emailField.getText().trim());
                        commande.setTotale(artwork.getPrix());
                        commande.setDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
                        commande.setStatus("En attente");
                        commande.setArtwork_id(artwork.getId());
                        
                        // Sauvegarder la commande
                        ServiceCommande serviceCommande = new ServiceCommande();
                        serviceCommande.add(commande);
                        
                        showAlert(Alert.AlertType.INFORMATION, "Commande Confirmée", 
                            "Votre commande a été enregistrée avec succès!\n\n" +
                            "Détails de la commande:\n" +
                            "- Client: " + commande.getNom() + "\n" +
                            "- Total: " + String.format("%.2f €", commande.getTotale()) + "\n" +
                            "- Date: " + commande.getDate() + "\n\n" +
                            "Un email de confirmation vous sera envoyé à " + commande.getEmail());
                        stage.close();
                        
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                                "Une erreur est survenue lors de l'enregistrement de la commande: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
            
            // Afficher la fenêtre
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de créer le formulaire de commande : " + e.getMessage());
        }
    }
    
    private boolean validateFields(TextField nomField, TextField prenomField, 
                                TextField adresseField, TextField telField, TextField emailField) {
        if (nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le nom est requis.");
            return false;
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le prénom est requis.");
            return false;
        }
        
        if (adresseField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "L'adresse de livraison est requise.");
            return false;
        }
        
        if (telField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le numéro de téléphone est requis.");
            return false;
        }
        
        if (!telField.getText().matches("\\d{8}")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le numéro de téléphone doit contenir 8 chiffres.");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty() || 
            !emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez entrer une adresse email valide.");
            return false;
        }
        
        return true;
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
        Artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

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
        Artwork selectedArtwork = tableView.getSelectionModel().getSelectedItem();

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
        List<Artwork> artworks = service.getAll();
        tableView.setItems(FXCollections.observableArrayList(artworks));
    }

    private void loadArtworks() {
        try {
            List<Artwork> artworks = service.getAll();
            artworkGrid.getChildren().clear();
            
            int column = 0;
            int row = 0;
            
            for (Artwork artwork : artworks) {
                VBox artworkBox = createArtworkBox(artwork);
                
                artworkGrid.add(artworkBox, column, row);
                
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la chargement des œuvres: " + e.getMessage());
        }
    }

    private VBox createArtworkBox(Artwork artwork) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("artwork-box");
        box.setPrefWidth(280);
        
        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(artwork.getImage());
            imageView.setImage(image);
            imageView.setFitWidth(250);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }
        
        // Informations
        Text title = new Text(artwork.getTitre());
        title.getStyleClass().add("artwork-title");
        
        Text artist = new Text("Par " + artwork.getArtistenom());
        artist.getStyleClass().add("artwork-artist");
        
        Text price = new Text(String.format("%.2f €", artwork.getPrix()));
        price.getStyleClass().add("artwork-price");
        
        Text status = new Text(artwork.getStatus());
        status.getStyleClass().add("artwork-status");
        
        box.getChildren().addAll(imageView, title, artist, price, status);
        
        return box;
    }
}
