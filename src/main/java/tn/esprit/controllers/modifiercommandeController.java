package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.Commande;
import tn.esprit.services.ServiceCommande;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class modifiercommandeController {

    @FXML
    private TextField artwork_idTextField;

    @FXML
    private TextField nomTextField;

    @FXML
    private TextField adressTextField;

    @FXML
    private TextField telephoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private TextField totaleTextField;



    @FXML
    private Button btnSave;

    private Commande currentCommande;
    private Object parentController;

    public void setCommande(Commande c) {
        this.currentCommande = c;
        populateFields();
    }

    public void setParentController(Object controller) {
        this.parentController = controller;
    }


    private void populateFields() {
        if (currentCommande != null) {
            artwork_idTextField.setText(String.valueOf(currentCommande.getArtwork_id()));
            nomTextField.setText(currentCommande.getNom());
            adressTextField.setText(currentCommande.getAdress());
            telephoneTextField.setText(currentCommande.getTelephone());
            emailTextField.setText(currentCommande.getEmail());

            // Vérification de date null
            if (currentCommande.getDate() != null) {
                dateDatePicker.setValue(currentCommande.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
            }

            totaleTextField.setText(String.valueOf(currentCommande.getTotale()));

            artwork_idTextField.setEditable(false);
        }
    }
    @FXML
    void updateCommande(ActionEvent event) {
        if (validateFields()) {
            try {
                currentCommande.setNom(nomTextField.getText());
                currentCommande.setAdress(adressTextField.getText());
                currentCommande.setTelephone(telephoneTextField.getText());
                currentCommande.setEmail(emailTextField.getText());
                currentCommande.setDate(Date.from(dateDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                currentCommande.setTotale(Double.parseDouble(totaleTextField.getText()));

                ServiceCommande service = new ServiceCommande();
                service.update(currentCommande);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "La commande a été modifiée avec succès!");

                // Rafraîchir la table dans le contrôleur parent
                if (parentController != null) {
                    if (parentController instanceof listcommandeController) {
                        ((listcommandeController) parentController).refreshTable();
                    } else if (parentController instanceof AdminDashboardController) {
                        ((AdminDashboardController) parentController).refreshOrderTable();
                    }
                }

                // Fermer la fenêtre
                closeWindow();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le total doit être un nombre valide.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        // Validation du nom (entre 3 et 50 caractères)
        if (nomTextField.getText().isEmpty()) {
            errorMessage.append("Le nom est obligatoire.\n");
        } else if (nomTextField.getText().length() < 3 || nomTextField.getText().length() > 50) {
            errorMessage.append("Le nom doit contenir entre 3 et 50 caractères.\n");
        }

        // Validation de l'adresse (entre 5 et 100 caractères)
        if (adressTextField.getText().isEmpty()) {
            errorMessage.append("L'adresse est obligatoire.\n");
        } else if (adressTextField.getText().length() < 5 || adressTextField.getText().length() > 100) {
            errorMessage.append("L'adresse doit contenir entre 5 et 100 caractères.\n");
        }

        // Validation du téléphone (format: 8 chiffres)
        if (telephoneTextField.getText().isEmpty()) {
            errorMessage.append("Le numéro de téléphone est obligatoire.\n");
        } else if (!telephoneTextField.getText().matches("^[0-9]{8}$")) {
            errorMessage.append("Le numéro de téléphone doit contenir exactement 8 chiffres.\n");
        }

        // Validation de l'email
        if (emailTextField.getText().isEmpty()) {
            errorMessage.append("L'email est obligatoire.\n");
        } else if (!emailTextField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorMessage.append("Format d'email invalide.\n");
        }

        // Validation de la date
        if (dateDatePicker.getValue() == null) {
            errorMessage.append("La date est obligatoire.\n");
        } else {
            LocalDate selectedDate = dateDatePicker.getValue();
            LocalDate today = LocalDate.now();
            if (selectedDate.isAfter(today)) {
                errorMessage.append("La date ne peut pas être dans le futur.\n");
            }
        }

        // Validation du montant total
        if (totaleTextField.getText().isEmpty()) {
            errorMessage.append("Le montant total est obligatoire.\n");
        } else {
            try {
                double montant = Double.parseDouble(totaleTextField.getText());
                if (montant <= 0) {
                    errorMessage.append("Le montant total doit être supérieur à 0.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le montant total doit être un nombre valide.\n");
            }
        }

        // Si des erreurs ont été trouvées
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
            return false;
        }
        
        return true;
    }

    // Méthodes utilitaires pour la validation
    private void setupValidationListeners() {
        // Validation en temps réel du téléphone
        telephoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                telephoneTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                telephoneTextField.setText(oldValue);
            }
        });

        // Validation en temps réel du montant
        totaleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                totaleTextField.setText(oldValue);
            }
        });

        // Validation en temps réel du nom
        nomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                nomTextField.setText(oldValue);
            }
        });

        // Validation en temps réel de l'adresse
        adressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                adressTextField.setText(oldValue);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        setupValidationListeners();
    }
} 