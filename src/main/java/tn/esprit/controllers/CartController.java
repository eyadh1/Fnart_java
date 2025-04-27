package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Artwork;
import tn.esprit.utils.CartManager;

import java.io.IOException;
import java.util.List;

public class CartController {
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button orderButton;

    private List<Artwork> cartItems;
    private double totalPrice;
    @FXML
    private Button continueButton;
    @FXML
    public void initialize() {
        refreshCart();
    }

    public void refreshCart() {
        cartItems = CartManager.getCartItems();
        if (cartItems == null) {
            System.out.println("cartItems is null, initializing empty list.");
            cartItems = new java.util.ArrayList<>();
        }
        updateDisplay();
    }

    public void addItem(Artwork artwork) {
        if (cartItems == null) {
            cartItems = new java.util.ArrayList<>();
        }
        cartItems.add(artwork);
        double itemPrice = artwork.getPrix();
        totalPrice += itemPrice;
        updateDisplay();
    }

    private void updateDisplay() {
        if (cartItemsContainer == null) {
            System.out.println("cartItemsContainer is null!");
            return;
        }
        cartItemsContainer.getChildren().clear();
        totalPrice = 0;

        if (cartItems == null) {
            System.out.println("cartItems is null in updateDisplay!");
            return;
        }

        for (Artwork item : cartItems) {
            if (item == null) {
                System.out.println("Null artwork in cartItems!");
                continue;
            }
            System.out.println("Artwork: titre=" + item.getTitre() + ", prix=" + item.getPrix() + ", image=" + item.getImage());

            HBox itemBox = new HBox(20);
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.getStyleClass().add("cart-item");

            // Image
            ImageView imageView = new ImageView();
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                try {
                    imageView.setImage(new Image("file:C:/xampp/htdocs/artwork_images/" + item.getImage()));
                } catch (Exception e) {
                    System.out.println("Erreur chargement image: " + e.getMessage());
                }
            }

            // Infos
            VBox infoBox = new VBox(5);
            Label titleLabel = new Label(item.getTitre() != null ? item.getTitre() : "Titre inconnu");
            titleLabel.getStyleClass().add("title-label");
            Label descLabel = new Label(item.getDescription() != null ? item.getDescription() : "");
            descLabel.getStyleClass().add("desc-label");
            infoBox.getChildren().addAll(titleLabel, descLabel);

            // Prix unitaire
            final double prix = item.getPrix();
            Label priceLabel = new Label(String.format("%,.3f DT", prix));
            priceLabel.getStyleClass().add("price-label");

            // Quantité
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, 1);
            quantitySpinner.getStyleClass().add("quantity-spinner");

            // Prix total ligne
            Label totalItemLabel = new Label();
            totalItemLabel.getStyleClass().add("total-label");

            // Calcul du total pour l'article
            quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                double itemTotal = newVal * prix;
                totalItemLabel.setText(String.format("%,.3f DT", itemTotal));
                updateTotalPrice();
            });
            totalItemLabel.setText(String.format("%,.3f DT", prix));

            // Bouton supprimer
            Button removeButton = new Button();
            removeButton.getStyleClass().add("remove-button");
            removeButton.setText("🗑");
            removeButton.setOnAction(e -> removeItem(item));

            // Ajout à la ligne
            itemBox.getChildren().addAll(imageView, infoBox, priceLabel, quantitySpinner, totalItemLabel, removeButton);
            cartItemsContainer.getChildren().add(itemBox);

            // Initialiser le calcul du total pour l'article
            quantitySpinner.getValueFactory().setValue(1);
        }

        updateTotalPrice();
    }
    private void updateTotalPrice() {
        totalPrice = 0;
        if (cartItemsContainer == null) return;
        int i = 0;
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox itemBox = (HBox) node;
                Spinner<Integer> spinner = (Spinner<Integer>) itemBox.getChildren().get(3);
                double price = cartItems.get(i).getPrix();
                totalPrice += spinner.getValue() * price;
                i++;
            }
        }
        if (totalPriceLabel != null)
            totalPriceLabel.setText(String.format("%,.3f DT", totalPrice));
    }
    private void removeItem(Artwork item) {
        if (cartItems != null) {
            cartItems.remove(item);
            totalPrice -= item.getPrix();
            updateDisplay();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleContinueShopping() {
        // Ferme la fenêtre du panier
        Stage stage = (Stage) continueButton.getScene().getWindow();
        stage.close();
        // Optionnel : tu peux aussi forcer le focus sur la fenêtre principale si besoin
    }

    @FXML
    private void handleOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutCommande.fxml"));
            Parent root = loader.load();

            AjoutCommandeController controller = loader.getController();
            if (controller != null) {
                controller.setCartItems(cartItems);
            }

            Stage stage = new Stage();
            stage.setTitle("Finaliser la commande");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

            // Ferme la fenêtre du panier
            Stage cartStage = (Stage) orderButton.getScene().getWindow();
            cartStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande : " + e.getMessage());
        }
    }
}