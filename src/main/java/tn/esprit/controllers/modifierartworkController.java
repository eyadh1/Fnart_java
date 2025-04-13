package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

public class modifierartworkController {

    private artwork currentArtwork;

    @FXML
    private TextField artistenomTextField;

    @FXML
    private Button btnsave;

    @FXML
    private TextField descriprtionTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField titreTextField;

    // Cette méthode est appelée depuis listartworkController pour transmettre l'œuvre sélectionnée
    public void setArtwork(artwork a) {
        this.currentArtwork = a;
        titreTextField.setText(a.getTitre());
        descriprtionTextField.setText(a.getDescription());
        prixTextField.setText(String.valueOf(a.getPrix()));
        imageTextField.setText(a.getImage());
        artistenomTextField.setText(a.getArtistenom());
        statusTextField.setText(a.getStatus());
    }

    @FXML
    private void modifierArtwork() {
        try {
            // Récupérer les nouvelles valeurs des champs texte
            currentArtwork.setTitre(titreTextField.getText());
            currentArtwork.setDescription(descriprtionTextField.getText());
            currentArtwork.setPrix(Integer.parseInt(prixTextField.getText()));
            currentArtwork.setImage(imageTextField.getText());
            currentArtwork.setArtistenom(artistenomTextField.getText());
            currentArtwork.setStatus(statusTextField.getText());

            // Appeler le service pour effectuer la mise à jour
            new serviceartwork().update(currentArtwork);

            // Afficher un message de succès
            showAlert("Succès", "Œuvre modifiée avec succès !");

            // Fermer la fenêtre de modification après la mise à jour
            Stage stage = (Stage) titreTextField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte avec un titre et un message
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
