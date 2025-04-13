package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DetailartworkController {

    @FXML
    private TextField titreTextFiled;

    @FXML
    private TextField descriptionTextFiled;

    @FXML
    private TextField prixTextFiled;

    @FXML
    private TextField artistenomTextFiled;

    @FXML
    private TextField imageTextFiled;

    @FXML
    private TextField StatusTextFiled;

    public void setTitreTextFiled(String titre) {
        this.titreTextFiled.setText(titre);
    }

    public void setDescriptionTextFiled(String description) {
        this.descriptionTextFiled.setText(description);
    }

    public void setPrixTextFiled(String prix) {
        this.prixTextFiled.setText(prix);
    }

    public void setArtistenomTextFiled(String artistenom) {
        this.artistenomTextFiled.setText(artistenom);
    }

    public void setImageTextFiled(String image) {
        this.imageTextFiled.setText(image);
    }

    public void setStatusTextFiled(String status) {
        this.StatusTextFiled.setText(status);
    }
}
