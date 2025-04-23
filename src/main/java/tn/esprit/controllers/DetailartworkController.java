package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tn.esprit.models.Artwork;
import java.io.File;
import java.nio.file.Paths;

public class DetailartworkController {

    @FXML
    private ImageView artworkImage;

    @FXML
    private Label titleLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label techniqueLabel;

    @FXML
    private Label dimensionsLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private VBox detailsContainer;

    private Artwork artwork;

    public void initData(Artwork artwork) {
        this.artwork = artwork;
        updateUI();
    }

    private void updateUI() {
        if (artwork != null) {
            titleLabel.setText(artwork.getTitre());
            artistLabel.setText("Artiste: " + artwork.getArtistenom());
            priceLabel.setText("Prix: " + artwork.getPrix() + " DT");
            descriptionLabel.setText(artwork.getDescription());
//            statusLabel.setText("Statut: " + artwork.getStatus());

            if (locationLabel != null) {
                locationLabel.setText("Localisation: " + (artwork.getLocation() != null ? artwork.getLocation() : "Non spécifiée"));
            }
            if (techniqueLabel != null) {
                techniqueLabel.setText("Technique: " + (artwork.getTechnique() != null ? artwork.getTechnique() : "Non spécifiée"));
            }
            if (dimensionsLabel != null) {
                dimensionsLabel.setText("Dimensions: " + (artwork.getDimensions() != null ? artwork.getDimensions() : "Non spécifiées"));
            }
            if (yearLabel != null) {
                yearLabel.setText("Année: " + (artwork.getYear() != null ? artwork.getYear() : "Non spécifiée"));
            }

            // Gestion de l'image
            try {
                String imageName = artwork.getImage();
                if (imageName != null && !imageName.isEmpty()) {
                    // Essayer plusieurs chemins possibles
                    String[] possiblePaths = {
                        "C:/xampp/htdocs/artwork_images/" + imageName,
                        "artwork_images/" + imageName,
                        "/artwork_images/" + imageName,
                        "src/main/resources/artwork_images/" + imageName
                    };

                    boolean imageLoaded = false;
                    for (String path : possiblePaths) {
                        try {
                            File file = new File(path);
                            if (file.exists()) {
                                String imageUrl = file.toURI().toURL().toExternalForm();
                                Image image = new Image(imageUrl);
                                if (!image.isError()) {
                                    artworkImage.setImage(image);
                                    imageLoaded = true;
                                    System.out.println("Image chargée avec succès depuis : " + path);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Échec du chargement depuis : " + path);
                        }
                    }

                    if (!imageLoaded) {
                        System.out.println("Aucun chemin n'a fonctionné, utilisation de l'image par défaut");
                        loadDefaultImage();
                    }
                } else {
                    loadDefaultImage();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                e.printStackTrace();
                loadDefaultImage();
            }
        }
    }

    private void loadDefaultImage() {
        try {
            String defaultImagePath = "/images/default-artwork.png";
            Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
            artworkImage.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image par défaut : " + e.getMessage());
        }
    }
}
