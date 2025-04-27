package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import tn.esprit.models.Artwork;
import tn.esprit.services.serviceartwork;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javafx.geometry.Side;

import java.io.*;
import java.nio.file.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class FrontArtworkController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private TilePane artworkGrid;

    @FXML
    private ImageView profileImage;

    private final serviceartwork artworkService = new serviceartwork();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger l'image de profil (remplacez le chemin par le vôtre)
        Image img = new Image("file:src/main/resources/assets/1744.jpeg");
        profileImage.setImage(img);
        setupSearch();
        loadArtworks();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadArtworks();
        });
    }

    private void loadArtworks() {
        artworkGrid.getChildren().clear();
        List<Artwork> artworks = artworkService.getAll();

        // Filter by search
        String searchText = searchField.getText().toLowerCase();

        artworks = artworks.stream()
                .filter(artwork -> artwork.getTitre().toLowerCase().contains(searchText) ||
                        artwork.getDescription().toLowerCase().contains(searchText) ||
                        artwork.getArtistenom().toLowerCase().contains(searchText))
                .toList();

        // Configure the TilePane for Pinterest-like layout
        artworkGrid.setPrefColumns(4); // Adjust based on window width
        artworkGrid.setHgap(16);
        artworkGrid.setVgap(20);
        artworkGrid.setPadding(new Insets(16));

        // Create card for each artwork
        for (Artwork artwork : artworks) {
            VBox pinCard = createPinCard(artwork);
            artworkGrid.getChildren().add(pinCard);
        }
    }

    private VBox createPinCard(Artwork artwork) {
        VBox card = new VBox(0);
        card.getStyleClass().add("pin-card");
        card.setMaxWidth(236);  // Standard Pinterest card width

        // Image container with overlay buttons
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("pin-image-container");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(236);  // Fixed width as in Pinterest
        imageView.setPreserveRatio(true);  // Maintain aspect ratio
        imageView.getStyleClass().add("pin-image");

        // Set clip to round the corners
        Rectangle clip = new Rectangle(236, 800);  // Height will adjust with aspect ratio
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        try {
            String imagePath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
            Image image = new Image("file:" + imagePath);
            imageView.setImage(image);

            // Adjust clip height based on loaded image
            clip.setHeight(imageView.getBoundsInLocal().getHeight());
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        // Conteneur pour le bouton Save en haut
        HBox topButtons = new HBox(8);
        topButtons.getStyleClass().add("top-buttons");
        topButtons.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(topButtons, Pos.TOP_RIGHT);

        // Bouton Save
        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("save-button");
        topButtons.getChildren().add(saveButton);
        // Conteneur pour les boutons en bas
        HBox bottomButtons = new HBox(4);
        bottomButtons.getStyleClass().add("bottom-buttons");
        bottomButtons.setAlignment(Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(bottomButtons, Pos.BOTTOM_RIGHT);

        // Bouton Share
        Button shareButton = new Button("↗");
        shareButton.getStyleClass().add("action-button");

        // Bouton More (trois points)
        Button moreButton = new Button("⋮");
        moreButton.getStyleClass().add("action-button");

        bottomButtons.getChildren().addAll(shareButton, moreButton);

        // Menu contextuel pour les options principales
        ContextMenu moreMenu = new ContextMenu();
        MenuItem hideItem = new MenuItem("Masquer");
        MenuItem downloadItem = new MenuItem("Télécharger");
        MenuItem reportItem = new MenuItem("Signaler");
        moreMenu.getItems().addAll(hideItem, downloadItem, reportItem);

        // Menu contextuel pour le partage
        ContextMenu shareMenu = new ContextMenu();
        MenuItem copyLinkItem = new MenuItem("Copier le lien");
        MenuItem whatsappItem = new MenuItem("WhatsApp");
        MenuItem messengerItem = new MenuItem("Messenger");
        MenuItem facebookItem = new MenuItem("Facebook");
        MenuItem xItem = new MenuItem("X");
        shareMenu.getItems().addAll(copyLinkItem, whatsappItem, messengerItem, facebookItem, xItem);

        // Actions des boutons
        moreButton.setOnAction(e -> {
            moreMenu.show(moreButton, javafx.geometry.Side.BOTTOM, -140, 5);
            e.consume(); // Empêcher la propagation du clic
        });

        shareButton.setOnAction(e -> {
            shareMenu.show(shareButton, javafx.geometry.Side.BOTTOM, -140, 5);
            e.consume(); // Empêcher la propagation du clic
        });

        // Actions des items du menu More
        hideItem.setOnAction(e -> handleHide(artwork));
        downloadItem.setOnAction(e -> handleDownload(artwork));
        reportItem.setOnAction(e -> handleReport(artwork));

        // Actions des items du menu Share
        copyLinkItem.setOnAction(e -> handleCopyLink(artwork));
        whatsappItem.setOnAction(e -> handleWhatsAppShare(artwork));
        messengerItem.setOnAction(e -> handleMessengerShare(artwork));
        facebookItem.setOnAction(e -> handleFacebookShare(artwork));
        xItem.setOnAction(e -> handleXShare(artwork));

        // Compact title and artist labels
        VBox infoBox = new VBox(2);  // Reduced spacing between labels
        infoBox.setPadding(new Insets(8, 8, 8, 8));  // Compact padding

        Label titleLabel = new Label(artwork.getTitre());
        titleLabel.getStyleClass().add("pin-title");
        titleLabel.setWrapText(true);

        Label artistLabel = new Label(artwork.getArtistenom());
        artistLabel.getStyleClass().add("pin-artist");

        infoBox.getChildren().addAll(titleLabel, artistLabel);

        // Configure image click
        imageView.setOnMouseClicked(e -> {
            showArtworkDetails(artwork);
            e.consume();
        });

        // Add click to entire card except buttons
        card.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof ImageView || e.getTarget() instanceof VBox || e.getTarget() instanceof Label) {
                showArtworkDetails(artwork);
            }
        });

        // Ajoute tous les éléments à l'image container
        imageContainer.getChildren().addAll(imageView, topButtons, bottomButtons);

        // Add only necessary components to card
        card.getChildren().addAll(imageContainer, infoBox);
        return card;
    }
    private void handleSave(Artwork artwork) {
        // Implémenter la logique de sauvegarde
        showAlert("Succès", "Œuvre enregistrée dans votre collection");
    }
    private void showShareMenu(Artwork artwork) {
        Stage shareStage = new Stage();
        shareStage.initModality(Modality.APPLICATION_MODAL);
        shareStage.initStyle(StageStyle.UNDECORATED);

        VBox shareBox = new VBox(10);
        shareBox.setPadding(new Insets(15));
        shareBox.getStyleClass().add("share-menu");
        shareBox.setStyle("-fx-background-color: white; -fx-border-color: red; -fx-border-width: 2px;");

        // En-tête avec titre et bouton de fermeture
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPrefWidth(Double.MAX_VALUE);

        Label titleLabel = new Label("Share");
        titleLabel.getStyleClass().add("share-title");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER);

        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> shareStage.close());

        header.getChildren().addAll(titleLabel, closeButton);

        // Options de partage
        HBox shareOptions = new HBox(15);
        shareOptions.setAlignment(Pos.CENTER);

        // Copier le lien
        VBox copyLink = createSimpleShareOption("Copy link", "ICON", e -> {
            handleCopyLink(artwork);
            shareStage.close();
        });

        // WhatsApp
        VBox whatsApp = createSimpleShareOption("WhatsApp", "ICON", e -> {
            handleWhatsAppShare(artwork);
            shareStage.close();
        });

        // Messenger
        VBox messenger = createSimpleShareOption("Messenger", "ICON", e -> {
            handleMessengerShare(artwork);
            shareStage.close();
        });

        // Facebook
        VBox facebook = createSimpleShareOption("Facebook", "ICON", e -> {
            handleFacebookShare(artwork);
            shareStage.close();
        });

        // X (Twitter)
        VBox twitter = createSimpleShareOption("X", "ICON", e -> {
            handleXShare(artwork);
            shareStage.close();
        });

        shareOptions.getChildren().addAll(copyLink, whatsApp, messenger, facebook, twitter);

        // Champ de recherche
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or email");
        searchField.getStyleClass().add("share-search");

        shareBox.getChildren().addAll(header, shareOptions, new Separator(), searchField);
        shareBox.getChildren().add(new Label("TEST SHARE"));

        Scene scene = new Scene(shareBox);
        // scene.getStylesheets().add(getClass().getResource("/styles/pinterest-style.css").toExternalForm());

        shareStage.setScene(scene);
        shareStage.show();
    }
    private void showArtworkDetails(Artwork artwork) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detailartwork.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            
            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Détails de l'œuvre");
            
            // Configurer la fenêtre
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMaximized(true);

            // Récupérer et initialiser le contrôleur
            DetailartworkController controller = loader.getController();
            if (controller == null) {
                throw new IOException("Cannot get controller for DetailartworkController");
            }
            controller.initData(artwork);

            // Afficher la fenêtre
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
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
    private void handleForum() {
        showAlert("Info", "Page Forum à venir");
    }
    @FXML
    private void handleCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            // Get the controller and refresh the cart
            CartController controller = loader.getController();
            if (controller != null) {
                controller.refreshCart();
            }

            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le panier : " + e.getMessage());
        }
    }

    private void handleHide(Artwork artwork) {
        // Implémenter la logique pour masquer l'œuvre
        showAlert("Info", "Cette œuvre ne sera plus affichée dans votre flux");
    }

    private void handleDownload(Artwork artwork) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox downloadMenu = new VBox(8);
        downloadMenu.getStyleClass().add("download-menu");
        downloadMenu.setPadding(new Insets(10));

        Label imageOption = new Label("Télécharger l'image");
        imageOption.getStyleClass().add("download-option");
        
        Label pdfOption = new Label("Télécharger en PDF");
        pdfOption.getStyleClass().add("download-option");

        imageOption.setOnMouseClicked(e -> {
            downloadImage(artwork);
            popupStage.close();
        });

        pdfOption.setOnMouseClicked(e -> {
            downloadPDF(artwork);
            popupStage.close();
        });

        downloadMenu.getChildren().addAll(imageOption, pdfOption);

        Scene scene = new Scene(downloadMenu);
        scene.getStylesheets().add(getClass().getResource("/styles/pinterest-style.css").toExternalForm());
        
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void downloadImage(Artwork artwork) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        fileChooser.setInitialFileName(artwork.getTitre() + ".jpg");

        File destFile = fileChooser.showSaveDialog(null);
        if (destFile != null) {
            try {
                String sourcePath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
                Files.copy(Paths.get(sourcePath), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert("Succès", "Image téléchargée avec succès !");
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors du téléchargement de l'image : " + e.getMessage());
            }
        }
    }

    private void downloadPDF(Artwork artwork) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer en PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        fileChooser.setInitialFileName(artwork.getTitre() + ".pdf");

        File destFile = fileChooser.showSaveDialog(null);
        if (destFile != null) {
            try {
                createPDF(artwork, destFile);
                showAlert("Succès", "PDF créé avec succès !");
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de la création du PDF : " + e.getMessage());
            }
        }
    }

    private void createPDF(Artwork artwork, File destFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            String imagePath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
            PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();

            float ratio = Math.min(pageWidth / imageWidth, pageHeight / imageHeight) * 0.8f;
            float scaledWidth = imageWidth * ratio;
            float scaledHeight = imageHeight * ratio;

            float x = (pageWidth - scaledWidth) / 2;
            float y = (pageHeight - scaledHeight) / 2;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(image, x, y, scaledWidth, scaledHeight);
            }

            document.save(destFile);
        }
    }

    private void handleReport(Artwork artwork) {
        // Implémenter la logique de signalement
        showAlert("Info", "Merci de votre signalement. Nous allons examiner cette œuvre.");
    }

    private void handleCopyLink(Artwork artwork) {
        // Simuler un lien pour l'exemple
        String link = "http://votre-site.com/artwork/" + artwork.getId();
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(link);
        clipboard.setContent(content);
        showAlert("Succès", "Lien copié dans le presse-papiers");
    }

    private void handleWhatsAppShare(Artwork artwork) {
        String url = "https://wa.me/?text=" + encodeUrl("Découvrez cette œuvre d'art : " + artwork.getTitre());
        openUrl(url);
    }

    private void handleMessengerShare(Artwork artwork) {
        String url = "https://www.messenger.com/share?link=" + encodeUrl("http://votre-site.com/artwork/" + artwork.getId());
        openUrl(url);
    }

    private void handleFacebookShare(Artwork artwork) {
        String url = "https://www.facebook.com/sharer/sharer.php?u=" + encodeUrl("http://votre-site.com/artwork/" + artwork.getId());
        openUrl(url);
    }

    private void handleXShare(Artwork artwork) {
        String url = "https://twitter.com/intent/tweet?text=" + encodeUrl("Découvrez cette œuvre d'art : " + artwork.getTitre());
        openUrl(url);
    }

    private String encodeUrl(String url) {
        try {
            return java.net.URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    private void openUrl(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le lien : " + e.getMessage());
        }
    }

    // Helper method to create a share option button
    private VBox createSimpleShareOption(String label, String icon, javafx.event.EventHandler<javafx.scene.input.MouseEvent> handler) {
        VBox optionBox = new VBox(2);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.getStyleClass().add("share-option");

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("share-icon");
        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("share-label");

        optionBox.getChildren().addAll(iconLabel, textLabel);
        optionBox.setOnMouseClicked(handler);
        optionBox.setCursor(javafx.scene.Cursor.HAND);
        return optionBox;
    }
}

