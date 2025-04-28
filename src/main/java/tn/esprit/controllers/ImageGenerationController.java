package tn.esprit.controllers;

// Imports (Assurez-vous que tous les imports nécessaires sont présents)
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import tn.esprit.services.ImageGenerationService; // Assurez-vous que le chemin est correct
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;

public class ImageGenerationController {

    @FXML private TextField promptField;
    @FXML private Button generateBtn;
    @FXML private Label errorLabel;
    @FXML private ImageView resultImageView;
    @FXML private RadioButton standardModel;
    @FXML private RadioButton hdModel;
    @FXML private RadioButton geniusModel; // Désactivé car pas de mapping direct DALL-E 3
    @FXML private RadioButton speedPref;
    @FXML private RadioButton qualityPref;
    @FXML private CheckBox oldeModelCheck;
    @FXML private ImageView style1;
    @FXML private ImageView style2;
    @FXML private ImageView style3;
    @FXML private ProgressIndicator loadingIndicator; // Assurez-vous qu'il est dans le FXML
    @FXML private Label promptDisplayLabel;
    @FXML private Button downloadBtn;
    @FXML private Button sideHandle;
    @FXML private VBox sidebar;

    private final ImageGenerationService imageGenerationService = new ImageGenerationService();
    private final ToggleGroup qualityGroup = new ToggleGroup();
    private final ToggleGroup preferenceGroup = new ToggleGroup();
    private int selectedStyle = 1;
    private String lastPrompt = "";
    private Image lastImage = null;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true); // [5, 6, 7]
            return t;
        }
    });

    @FXML
    public void initialize() {
        // Groupes de boutons radio pour la qualité
        standardModel.setToggleGroup(qualityGroup);
        hdModel.setToggleGroup(qualityGroup);
        geniusModel.setToggleGroup(qualityGroup);
        geniusModel.setDisable(true);
        standardModel.setSelected(true);
        standardModel.setUserData("standard");
        hdModel.setUserData("hd");
        // Dans la méthode initialize():
        generateBtn.getStyleClass().add("generate-button");
        downloadBtn.getStyleClass().add("download-button");
        promptField.getStyleClass().add("text-field");
        errorLabel.getStyleClass().add("error-label");
        // Groupe pour la préférence
        speedPref.setToggleGroup(preferenceGroup);
        qualityPref.setToggleGroup(preferenceGroup);
        speedPref.setSelected(true);

        // Initialiser les styles
        loadStyleImage(style1, "/assets/style1.png");
        loadStyleImage(style2, "/assets/style2.png");
        loadStyleImage(style3, "/assets/style3.png");

        selectStyle(1);
        style1.setOnMouseClicked(e -> selectStyle(1));
        style2.setOnMouseClicked(e -> selectStyle(2));
        style3.setOnMouseClicked(e -> selectStyle(3));

        generateBtn.setOnAction(e -> onGenerate());
        downloadBtn.setOnAction(e -> {
            if (lastImage != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Enregistrer l'image générée");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
                File file = fileChooser.showSaveDialog(downloadBtn.getScene().getWindow());
                if (file != null) {
                    try {
                        BufferedImage bImage = SwingFXUtils.fromFXImage(lastImage, null);
                        ImageIO.write(bImage, "png", file);
                    } catch (IOException ex) {
                        showAlert(AlertType.ERROR, "Erreur", "Impossible de sauvegarder l'image : " + ex.getMessage());
                    }
                }
            }
        });

        if (loadingIndicator!= null) {
            loadingIndicator.setVisible(false);
        }
    }

    private void loadStyleImage(ImageView imageView, String resourcePath) {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is != null) {
            imageView.setImage(new Image(is));
        } else {
            imageView.setImage(null);
            System.err.println("[ERREUR] Image de style manquante : " + resourcePath + " (placez-la dans src/main/resources" + resourcePath + ")");
        }
    }

    private void selectStyle(int styleNum) {
        selectedStyle = styleNum;
        style1.getStyleClass().remove("selected-style");
        style2.getStyleClass().remove("selected-style");
        style3.getStyleClass().remove("selected-style");

        switch(styleNum) {
            case 1:
                style1.getStyleClass().add("selected-style");
                break;
            case 2:
                style2.getStyleClass().add("selected-style");
                break;
            case 3:
                style3.getStyleClass().add("selected-style");
                break;
        }
    }
    private void onGenerate() {
        errorLabel.setText("");
        resultImageView.setImage(null);
        String prompt = promptField.getText(); // [11, 12, 13]
        if (prompt == null || prompt.trim().isEmpty()) {
            errorLabel.setText("Veuillez entrer un prompt.");
            return;
        }

        // --- Récupération des options ---
        String qualityOption = (String) qualityGroup.getSelectedToggle().getUserData(); // "standard" ou "hd"
        boolean useDalle2 = oldeModelCheck.isSelected();
        String modelOption = useDalle2? "dall-e-2" : "dall-e-3"; // [14, 10, 15]

        String styleOptionApiValue;
        String finalPrompt = prompt; // Utiliser une nouvelle variable pour le prompt potentiellement modifié

        if (modelOption.equals("dall-e-3")) {
            if (selectedStyle == 1) {
                styleOptionApiValue = "vivid"; // [14, 9, 10]
            } else if (selectedStyle == 2) {
                styleOptionApiValue = "natural"; // [14, 9, 10]
            } else {
                styleOptionApiValue = "vivid"; // Défaut
                finalPrompt += " dans un style artistique spécifique numéro 3";
            }
        } else {
            styleOptionApiValue = null; // DALL-E 2 ne supporte pas 'style'
            finalPrompt += " dans le style visuel numéro " + selectedStyle;
        }

        // Déterminer la taille
        String sizeOption = "1024x1024"; // Taille par défaut
        if ("dall-e-2".equals(modelOption)) {
            // Options DALL-E 2: "256x256", "512x512", "1024x1024" [14, 10]
            sizeOption = "1024x1024"; // ou une autre taille supportée par DALL-E 2
        } else {
            // Options DALL-E 3: "1024x1024", "1792x1024", "1024x1792" [8, 9, 10]
            sizeOption = "1024x1024"; // ou une autre taille supportée par DALL-E 3
        }

        // Déterminer le format de réponse
        String responseFormatOption = "url"; // ou "b64_json" [8, 9, 10]

        // --- Récupération de la clé API ---
        String apiKeyOption = retrieveApiKeySecurely(); // [16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
        if (apiKeyOption == null || apiKeyOption.trim().isEmpty() || apiKeyOption.equals("VOTRE_CLÉ_API_ICI")) {
            showAlert(AlertType.ERROR, "Erreur de Configuration", "La clé API n'est pas configurée correctement.");
            return;
        }

        // Désactiver le bouton et montrer l'indicateur
        generateBtn.setDisable(true);
        if (loadingIndicator!= null) {
            loadingIndicator.setVisible(true);
        }

        // --- Création des variables (effectivement) finales pour la Task ---
        final String taskPrompt = finalPrompt;
        final String taskApiKey = apiKeyOption;
        final String taskModel = modelOption;
        final String taskQuality = ("dall-e-3".equals(taskModel))? qualityOption : null;
        final String taskStyle = ("dall-e-3".equals(taskModel))? styleOptionApiValue : null;
        final String taskSize = sizeOption;
        final String taskResponseFormat = responseFormatOption;

        // Créer et lancer la tâche en arrière-plan [26, 27, 5, 6]
        Task<String> imageGenerationTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                try {
                    // Utiliser les variables finales ici
                    return imageGenerationService.generateImageWithOptionsAsync(
                            taskPrompt,
                            taskApiKey,
                            taskModel,
                            taskQuality,
                            taskStyle,
                            taskSize,
                            taskResponseFormat
                    ).get(); //.get() bloque CE thread (de fond)

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof Exception) {
                        throw (Exception) cause;
                    } else {
                        throw new Exception("Erreur inconnue lors de l'exécution de la tâche.", e);
                    }
                } catch (InterruptedException e) {
                    if (isCancelled()) { // [6]
                        updateMessage("Génération interrompue.");
                        return null;
                    }
                    Thread.currentThread().interrupt();
                    throw new Exception("Thread interrompu pendant l'attente.", e);
                }
            }
        };

        // Gérer le succès (sur le thread UI JavaFX) [26, 27, 5, 6]
        imageGenerationTask.setOnSucceeded(workerStateEvent -> {
            String imageData = imageGenerationTask.getValue();
            if (imageData!= null &&!imageData.isEmpty()) {
                displayImage(imageData);
                errorLabel.setText("");
            } else if (!imageGenerationTask.isCancelled()) {
                showAlert(AlertType.WARNING, "Résultat Inattendu", "Aucune donnée d'image reçue.");
            }
            cleanupAfterTask();
        });

        // Gérer l'échec (sur le thread UI JavaFX) [26, 27, 5, 6]
        imageGenerationTask.setOnFailed(workerStateEvent -> {
            Throwable exception = imageGenerationTask.getException();
            System.err.println("Erreur lors de la génération de l'image: " + exception.getMessage());
            exception.printStackTrace();
            errorLabel.setText("Erreur: " + exception.getMessage());
            showAlert(AlertType.ERROR, "Erreur de Génération", "Impossible de générer l'image : " + exception.getMessage()); // [28, 29, 30, 31]
            cleanupAfterTask();
        });

        // Soumettre la tâche
        executorService.submit(imageGenerationTask);

        lastPrompt = prompt;
        promptDisplayLabel.setText("");
    }
    private void setLoading(boolean isLoading) {
        Platform.runLater(() -> {
            generateBtn.setDisable(isLoading);
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(isLoading);
                if (isLoading) {
                    loadingIndicator.getStyleClass().add("loading-indicator");
                } else {
                    loadingIndicator.getStyleClass().remove("loading-indicator");
                }
            }
        });
    }
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Affiche l'image reçue (URL ou Base64) dans l'ImageView.
     * @param imageData URL ou chaîne Base64 de l'image.
     */
 
    private void displayImage(String imageData) {
        try {
            Image image;
            if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                // C'est une URL, charger en arrière-plan [4, 5, 6, 7]
                image = new Image(imageData, true); // true pour background loading [5, 6]
            } else {
                // Supposer que c'est du Base64
                // 1. Décoder la chaîne Base64 en TABLEAU d'octets (byte[])
                byte[] imageBytes = Base64.getDecoder().decode(imageData);

                // 2. Vérifier si le tableau est valide (peut être null ou vide)
                if (imageBytes == null || imageBytes.length == 0) {
                    throw new IOException("Les données Base64 décodées sont vides ou null.");
                }

                // 3. Créer un InputStream à partir du TABLEAU d'octets
                try (InputStream stream = new ByteArrayInputStream(imageBytes)) {
                    // 4. Créer l'objet Image JavaFX à partir du stream
                    image = new Image(stream);
                }
                // 'stream' est automatiquement fermé ici grâce au try-with-resources
            }

            // Vérifier si le chargement/décodage de l'Image JavaFX a échoué [6]
            if (image.isError()) {
                System.err.println("Erreur de chargement/décodage de l'image JavaFX: " + image.getException());
                // Lancer l'exception pour qu'elle soit attrapée par le bloc catch externe
                throw image.getException();
            }

            // Appliquer les propriétés de l'ImageView pour l'affichage
            resultImageView.setPreserveRatio(true); // [8, 9]
            // Définissez ici la taille souhaitée pour l'affichage dans l'ImageView
            resultImageView.setFitWidth(400);
            resultImageView.setFitHeight(400);
            resultImageView.setSmooth(true); // Améliore la qualité si redimensionné [8, 9]

            // Définir l'image dans l'ImageView
            Platform.runLater(() -> {
                resultImageView.setImage(image);
                promptDisplayLabel.setText("Prompt : " + lastPrompt);
                lastImage = image;
                // Animation fade-in
                FadeTransition ft = new FadeTransition(Duration.millis(700), resultImageView);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            });

        } catch (IllegalArgumentException e) {
            // Attraper spécifiquement les erreurs de décodage Base64
            System.err.println("Erreur lors du décodage Base64: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de Données", "Les données de l'image reçues (Base64) sont invalides ou corrompues.");
        } catch (IOException e) {
            // Attraper les erreurs de lecture du stream
            System.err.println("Erreur d'entrée/sortie lors de la lecture des données de l'image: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de Lecture", "Impossible de lire les données de l'image.");
        } catch (Exception e) { // Attraper toute autre exception (ex: erreur de chargement Image)
            System.err.println("Erreur lors de l'affichage de l'image: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur d'Affichage", "Impossible d'afficher l'image : " + e.getMessage());
        }
    }
    private void cleanupAfterTask() {
        generateBtn.setDisable(false);
        if (loadingIndicator!= null) {
            loadingIndicator.setVisible(false);
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        // S'assurer que l'alerte est affichée sur le thread UI [26, 27, 5, 6, 39]
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(alertType); // [28, 29, 30, 31]
            alert.setTitle(title); // [28, 29]
            alert.setHeaderText(null); // [28, 29]
            alert.setContentText(message); // [28, 29, 30]
            alert.showAndWait(); // [28, 29]
        } else {
            Platform.runLater(() -> showAlert(alertType, title, message)); // [26, 27, 5, 6, 39]
        }
    }
    private void animateButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private String retrieveApiKeySecurely() {
        // Exemple de lecture depuis application.properties
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                java.util.Properties props = new java.util.Properties();
                props.load(input);
                String apiKey = props.getProperty("stability.api.key");
                if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("VOTRE_CLÉ_API_ICI")) {
                    return apiKey;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de la clé API : " + e.getMessage());
        }
        // Fallback : à remplacer par ta vraie clé API !
        return "VOTRE_CLÉ_API_ICI";
    }

    public void shutdown() {
        executorService.shutdownNow();
        System.out.println("ExecutorService arrêté.");
    }

    @FXML
    private void toggleSidebar() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
        // Optionnel : tu peux changer la couleur ou l'icône de la poignée ici
    }
}