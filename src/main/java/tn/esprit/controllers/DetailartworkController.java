package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import tn.esprit.models.Artwork;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import tn.esprit.utils.CartManager;

public class DetailartworkController {

    @FXML private ImageView artworkImage;
    @FXML private Label artworkTitle;
    @FXML private Label artworkArtist;
    @FXML private Label artworkDescription;
    @FXML private Label artworkPrice;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartButton;
    @FXML private Button saveButton;
    @FXML private Button shareButton;
    @FXML private Button backButton;
    @FXML private Button likeButton;
    @FXML private Label likesCount;
    @FXML private Button quickSaveButton;
    @FXML private HBox recommendedItems;
    @FXML private Label limitedEditionBadge;
    @FXML private Label availabilityIndicator;
    @FXML private Label dimensionsLabel;
    @FXML private Label dateLabel;
    @FXML private Label techniqueLabel;
    @FXML private Label originalPrice;
    @FXML private Label promoBadge;
    @FXML private VBox exhibitionMapContainer;
    @FXML private ImageView mapImage;
    @FXML private Label exhibitionLocation;

    private Artwork artwork;
    private boolean isLiked = false;
    private int likes = 0;
    private Random random = new Random();

    public void initData(Artwork artwork) {
        this.artwork = artwork;
        if (artwork != null) {
            updateUI();
            setupRecommendations();
            if (dimensionsLabel != null) {
                dimensionsLabel.setText("Dimensions: " + artwork.getDimensions());
            }
            if (dateLabel != null) {
                dateLabel.setText("Date: " + artwork.getYear());
            }
            if (techniqueLabel != null) {
                techniqueLabel.setText("Technique: " + artwork.getTechnique());
            }
            if (availabilityIndicator != null) {
                availabilityIndicator.setText(
                    "disponible".equalsIgnoreCase(artwork.getStatus()) ? "Disponible" : "Indisponible"
                );
            }
            // if (exhibitionLocation != null) {
            //     exhibitionLocation.setText(artwork.getLieuExposition() != null ? artwork.getLieuExposition() : "");
            // }
            // if (mapImage != null && artwork.getMapImagePath() != null) {
            //     try {
            //         Image mapImg = new Image(artwork.getMapImagePath());
            //         mapImage.setImage(mapImg);
            //     } catch (Exception e) {
            //         System.err.println("Erreur chargement map image: " + e.getMessage());
            //     }
            // }
        }
    }

    @FXML
    public void initialize() {
        // Set up button actions
        if (addToCartButton != null) {
            addToCartButton.setOnAction(e -> handleAddToCart());
        }
        if (saveButton != null) {
            saveButton.setOnAction(e -> handleSave());
        }
        if (shareButton != null) {
            shareButton.setOnAction(e -> handleShare());
        }
        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        if (quickSaveButton != null) {
            quickSaveButton.setOnAction(e -> handleQuickSave());
        }

        // Handle like button icon loading
        if (likeButton != null) {
            try {
                // Try to load the heart image for the like button
                Image heartImage = new Image(getClass().getResourceAsStream("/images/heart.jpeg"));
                ImageView heartIcon = new ImageView(heartImage);
                heartIcon.setFitHeight(20);
                heartIcon.setFitWidth(20);
                likeButton.setGraphic(heartIcon);
            } catch (Exception e) {
                // Fallback to text if image can't be loaded
                likeButton.setText("♥");
                System.err.println("Impossible de charger l'image du cœur: " + e.getMessage());
            }

            // Set up like button functionality
            setupLikeButton();
        }

        // Set up spinner
        if (quantitySpinner != null) {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
            quantitySpinner.setValueFactory(valueFactory);
        }

        // Generate random likes count
        likes = 100 + random.nextInt(900);
        if (likesCount != null) {
            likesCount.setText(String.valueOf(likes));
        }
    }

    private void updateUI() {
        if (artwork == null) return;

        if (artworkTitle != null) {
            artworkTitle.setText(artwork.getTitre());
        }

        if (artworkArtist != null) {
            artworkArtist.setText("Artiste : " + artwork.getArtistenom());
        }

        if (artworkDescription != null) {
            artworkDescription.setText(artwork.getDescription());
        }

        if (artworkPrice != null) {
            artworkPrice.setText("Prix : " + artwork.getPrix() + " DT");
        }

        // Load artwork image
        if (artworkImage != null) {
            try {
                String localPath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
                File file = new File(localPath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    artworkImage.setImage(image);
                } else {
                    System.err.println("Image file not found: " + localPath);
                    // Use default image if available
                    try {
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-artwork.png"));
                        artworkImage.setImage(defaultImage);
                    } catch (Exception e) {
                        System.err.println("Default image not found either: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading artwork image: " + e.getMessage());
            }
        }
    }

    private void setupRecommendations() {
        if (recommendedItems == null) return;

        recommendedItems.getChildren().clear();

        // Add 3 placeholder image views as recommendations
        for (int i = 0; i < 3; i++) {
            ImageView imgView = new ImageView();
            imgView.setFitWidth(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);
            imgView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            // Set sample image - replace with actual related artwork
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-artwork.png"));
                imgView.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Could not load default recommendation image: " + e.getMessage());
            }

            recommendedItems.getChildren().add(imgView);
        }
    }

    private void setupLikeButton() {
        if (likeButton == null) return;

        likeButton.setOnAction(e -> {
            isLiked = !isLiked;
            if (isLiked) {
                likes++;
                likeButton.setStyle("-fx-background-color: #ffdddd;");
                // Update heart icon text if using text fallback
                if (likeButton.getText() != null && likeButton.getText().equals("♥")) {
                    likeButton.setText("❤");
                }
            } else {
                likes--;
                likeButton.setStyle("-fx-background-color: transparent;");
                // Reset heart icon text if using text fallback
                if (likeButton.getText() != null && likeButton.getText().equals("❤")) {
                    likeButton.setText("♥");
                }
            }
            if (likesCount != null) {
                likesCount.setText(String.valueOf(likes));
            }
        });
    }

    private void handleAddToCart() {
        int quantity = 1;
        if (quantitySpinner != null) {
            quantity = quantitySpinner.getValue();
        }

        // Add the current artwork to the CartManager
        for (int i = 0; i < quantity; i++) {
            CartManager.addToCart(artwork);
        }

        showAlert(Alert.AlertType.INFORMATION, "Succès",
                quantity + " article(s) ajouté(s) au panier !");
    }
    private void handleSave() {
        showAlert(Alert.AlertType.INFORMATION, "Succès",
                "Œuvre enregistrée dans votre collection");

        // Update save button to show saved state
        if (saveButton != null) {
            saveButton.setText("Saved");
            saveButton.setStyle("-fx-background-color: #e0e0e0;");
        }
    }

    private void handleQuickSave() {
        showAlert(Alert.AlertType.INFORMATION, "Succès",
                "Œuvre enregistrée rapidement!");

        // Update quick save button
        if (quickSaveButton != null) {
            quickSaveButton.setText("Saved");
            quickSaveButton.setStyle("-fx-background-color: #e0e0e0;");
        }
    }

    private void handleShare() {
        // Create sharing menu options similar to Pinterest
        ContextMenu shareMenu = new ContextMenu();

        MenuItem copyLinkItem = new MenuItem("Copier le lien");
        copyLinkItem.setOnAction(e -> {
            String link = "http://votre-site.com/artwork/" + (artwork != null ? artwork.getId() : "");
            ClipboardContent content = new ClipboardContent();
            content.putString(link);
            Clipboard.getSystemClipboard().setContent(content);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Lien copié dans le presse-papiers");
        });

        MenuItem facebookItem = new MenuItem("Facebook");
        MenuItem whatsappItem = new MenuItem("WhatsApp");
        MenuItem messengerItem = new MenuItem("Messenger");

        shareMenu.getItems().addAll(copyLinkItem, new SeparatorMenuItem(),
                facebookItem, whatsappItem, messengerItem);

        shareMenu.show(shareButton, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    private void handleBack() {
        if (backButton != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}