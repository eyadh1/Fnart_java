package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import tn.esprit.models.artwork;
import tn.esprit.service.serviceartwork;

import java.io.IOException;
import java.sql.SQLException;

public class ajouterartworkController {

    @FXML
    private Text ArtworkText;

    @FXML
    private TextField artistenomTextField;

    @FXML
    private Button btnSave;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField titreTextField;

    @FXML
    void creatartwork(ActionEvent event) {
        // Validation des champs
        if (titreTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() 
            || prixTextField.getText().isEmpty() || artistenomTextField.getText().isEmpty() 
            || imageTextField.getText().isEmpty() || statusTextField.getText().isEmpty()) {
            
            Alert validationAlert = new Alert(Alert.AlertType.ERROR);
            validationAlert.setTitle("Erreur");
            validationAlert.setHeaderText("Veuillez remplir tous les champs");
            validationAlert.show();
            return;
        }

        try {
            // Validation du prix
            int prix = Integer.parseInt(prixTextField.getText());
            if (prix <= 0) {
                Alert prixAlert = new Alert(Alert.AlertType.ERROR);
                prixAlert.setTitle("Erreur");
                prixAlert.setHeaderText("Le prix doit être supérieur à 0");
                prixAlert.show();
                return;
            }

            String titre = titreTextField.getText();
            String description = descriptionTextField.getText();
            String artistenom = artistenomTextField.getText();
            String image = imageTextField.getText();
            String status = statusTextField.getText();

            artwork artwork = new artwork(titre, description, prix, image, artistenom, status);
            serviceartwork serviceartwork = new serviceartwork();
            serviceartwork.add(artwork);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Artwork ajouté avec succès !");
            successAlert.show();

            // Navigation vers la vue de détails
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detailartwork.fxml"));
                Parent root = loader.load();
                DetailartworkController detailartworkController = loader.getController();
                
                // Mise à jour des champs de la vue de détails
                detailartworkController.setTitreTextFiled(titre);
                detailartworkController.setDescriptionTextFiled(description);
                detailartworkController.setPrixTextFiled(String.valueOf(prix));
                detailartworkController.setArtistenomTextFiled(artistenom);
                detailartworkController.setImageTextFiled(image);
                detailartworkController.setStatusTextFiled(status);
                
                // Changement de la scène
                titreTextField.getScene().setRoot(root);
            } catch (IOException e) {
                Alert navigationAlert = new Alert(Alert.AlertType.ERROR);
                navigationAlert.setTitle("Erreur");
                navigationAlert.setHeaderText("Erreur lors du chargement de la vue de détails");
                navigationAlert.setContentText(e.getMessage());
                navigationAlert.show();
            }
        } catch (NumberFormatException e) {
            Alert formatAlert = new Alert(Alert.AlertType.ERROR);
            formatAlert.setTitle("Erreur");
            formatAlert.setHeaderText("Le prix doit être un nombre valide");
            formatAlert.show();
        } catch (SQLException e) {
            Alert sqlAlert = new Alert(Alert.AlertType.ERROR);
            sqlAlert.setTitle("Erreur");
            sqlAlert.setHeaderText("Erreur lors de l'ajout de l'artwork");
            sqlAlert.setContentText(e.getMessage());
            sqlAlert.show();
        }
    }
}
    


