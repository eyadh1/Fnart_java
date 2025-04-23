package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import tn.esprit.models.Artwork;
import tn.esprit.services.serviceartwork;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FrontArtworkController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ImageView featuredImage;

    @FXML
    private Label featuredTitle;

    @FXML
    private Label featuredArtist;

    @FXML
    private Label featuredDescription;

    @FXML
    private Button featuredFavorite;

    @FXML
    private GridPane artworkGrid;

    private final serviceartwork artworkService = new serviceartwork();
    private static final int COLUMNS = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSearch();
        setupFilters();
        loadArtworks();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadArtworks();
        });
    }

    private void setupFilters() {
        filterComboBox.getItems().addAll("Tous", "Disponibles", "En exposition");
        filterComboBox.setValue("Tous");
        filterComboBox.setOnAction(event -> loadArtworks());
    }

    private void loadArtworks() {
        artworkGrid.getChildren().clear();
        List<Artwork> artworks = artworkService.getAll();

        // Filtrer selon la recherche
        String searchText = searchField.getText().toLowerCase();
        String filter = filterComboBox.getValue();

        artworks = artworks.stream()
            .filter(artwork -> artwork.getTitre().toLowerCase().contains(searchText) ||
                             artwork.getDescription().toLowerCase().contains(searchText) ||
                             artwork.getArtistenom().toLowerCase().contains(searchText))
            .filter(artwork -> filter.equals("Tous") ||
                             (filter.equals("Disponibles") && "disponible".equalsIgnoreCase(artwork.getStatus())) ||
                             (filter.equals("En exposition") && "en_exposition".equalsIgnoreCase(artwork.getStatus())))
            .toList();

        // Afficher l'œuvre en vedette (la première)
        if (!artworks.isEmpty()) {
            Artwork featured = artworks.get(0);
            updateFeaturedArtwork(featured);

            // Afficher les autres œuvres dans la grille
            for (int i = 1; i < artworks.size(); i++) {
                VBox artworkCard = createArtworkCard(artworks.get(i));
                int row = (i - 1) / COLUMNS;
                int col = (i - 1) % COLUMNS;

                // Ajouter un décalage vertical pour les lignes paires
                if (row % 2 == 1) {
                    artworkCard.setTranslateY(30);
                }

                artworkGrid.add(artworkCard, col, row);
            }
        }
    }

    private void updateFeaturedArtwork(Artwork artwork) {
        try {
            String imagePath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
            featuredImage.setImage(new Image("file:" + imagePath));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        featuredTitle.setText(artwork.getTitre());
        featuredArtist.setText(artwork.getArtistenom());
        featuredDescription.setText(artwork.getDescription());
    }

    private VBox createArtworkCard(Artwork artwork) {
        VBox card = new VBox(10);
        card.getStyleClass().add("artwork-card");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(170);
        imageView.setFitHeight(170);
        imageView.getStyleClass().add("artwork-image");
        imageView.setPreserveRatio(true);

        try {
            String imagePath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
            Image image = new Image("file:" + imagePath);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        Label titleLabel = new Label(artwork.getTitre());
        titleLabel.getStyleClass().add("artwork-title");
        titleLabel.setWrapText(true);

        Label artistLabel = new Label(artwork.getArtistenom());
        artistLabel.getStyleClass().add("artwork-artist");
        artistLabel.setWrapText(true);

        Label priceLabel = new Label(artwork.getPrix() + " DT");
        priceLabel.getStyleClass().add("artwork-price");

        // Création du bouton panier
        Button cartButton = new Button("🛒");
        cartButton.getStyleClass().add("cart-button");
        cartButton.setOnAction(e -> addToCart(artwork));

        Label cartLabel = new Label("Ajouter au panier");
        cartLabel.getStyleClass().add("cart-label");

        // Conteneur pour le prix et le bouton panier
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.getChildren().addAll(priceLabel, cartButton, cartLabel);

        // Zone cliquable pour les détails
        VBox detailsArea = new VBox(5);
        detailsArea.getChildren().addAll(imageView, titleLabel, artistLabel);
        detailsArea.setOnMouseClicked(event -> showArtworkDetails(artwork));

        card.getChildren().addAll(detailsArea, actionBox);
        return card;
    }

    private void addToCart(Artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            CartController controller = loader.getController();
            controller.addItem(artwork);

            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du panier : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le panier. Veuillez réessayer.");
        }
    }

    private void showArtworkDetails(Artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detailartwork.fxml"));
            Parent root = loader.load();

            DetailartworkController controller = loader.getController();
            controller.initData(artwork);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'œuvre - " + artwork.getTitre());
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'afficher les détails de l'œuvre : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleOeuvres() {
        // Déjà sur la page des œuvres
    }

    @FXML
    private void handleAtelier() {
        showAlert("Info", "Page Atelier à venir");
    }

    @FXML
    private void handleDons() {
        showAlert("Info", "Page Dons à venir");
    }

    @FXML
    private void handleBeneficiaire() {
        showAlert("Info", "Page Bénéficiaire à venir");
    }

    @FXML
    private void handleForum() {
        showAlert("Info", "Page Forum à venir");
    }

    @FXML
    private void handleSettings() {
        showAlert("Info", "Page Paramètres à venir");
    }
}

