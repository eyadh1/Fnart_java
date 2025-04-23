package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Artwork;
import java.io.IOException;
import java.util.ArrayList;
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
    public void initialize() {
        cartItems = new ArrayList<>();
        updateDisplay();
    }

    public void addItem(Artwork artwork) {
        cartItems.add(artwork);
        double itemPrice = artwork.getPrix();
        totalPrice += itemPrice;
        updateDisplay();
    }

    private void updateDisplay() {
        cartItemsContainer.getChildren().clear();
        totalPrice = 0;

        for (Artwork item : cartItems) {
            VBox itemBox = new VBox(5);
            Label titleLabel = new Label(item.getTitre());
            double itemPrice = item.getPrix();
            Label priceLabel = new Label(String.format("%.2f TND", itemPrice));
            Button removeButton = new Button("Retirer");

            removeButton.setOnAction(e -> removeItem(item));

            itemBox.getChildren().addAll(titleLabel, priceLabel, removeButton);
            cartItemsContainer.getChildren().add(itemBox);

            totalPrice += itemPrice;
        }

        totalPriceLabel.setText(String.format("Total: %.2f TND", totalPrice));
        orderButton.setDisable(cartItems.isEmpty());
    }

    private void removeItem(Artwork item) {
        cartItems.remove(item);
        totalPrice -= item.getPrix();
        updateDisplay();
    }

    @FXML
    private void handleOrder() {
        try {
            System.out.println("Attempting to load AjoutCommande.fxml...");
            String fxmlPath = "/AjoutCommande.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Could not find FXML file at: " + fxmlPath);
            }
            System.out.println("FXML file found at: " + loader.getLocation());
            
            Parent root = loader.load();
            System.out.println("FXML file loaded successfully");
            
            AjoutCommandeController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Failed to get AjoutCommandeController instance");
            }
            System.out.println("Controller instance obtained");
            
            controller.setCartItems(cartItems);
            System.out.println("Cart items set in controller");
            
            Stage stage = new Stage();
            stage.setTitle("Finaliser la commande");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
            
            // Fermer la fenêtre du panier
            Stage cartStage = (Stage) orderButton.getScene().getWindow();
            cartStage.close();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du formulaire de commande : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande. Veuillez réessayer.\nDétails: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue s'est produite.\nDétails: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 