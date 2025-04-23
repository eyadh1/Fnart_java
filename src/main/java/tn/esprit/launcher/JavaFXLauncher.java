package tn.esprit.launcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import tn.esprit.controllers.Home;

/**
 * Classe de lancement spécifique pour JavaFX qui s'assure que tous les modules nécessaires sont chargés.
 * Cette classe peut être utilisée comme point d'entrée alternatif pour l'application.
 */
public class JavaFXLauncher extends Application {

    public static void main(String[] args) {
        System.out.println("Lancement de l'application via JavaFXLauncher");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("JavaFX version: " + System.getProperty("javafx.version"));
        
        // Vérifier si les modules JavaFX sont disponibles
        try {
            Class.forName("javafx.application.Application");
            System.out.println("Module javafx.application.Application trouvé");
        } catch (ClassNotFoundException e) {
            System.err.println("Module javafx.application.Application non trouvé!");
            System.err.println("Veuillez vérifier que JavaFX est correctement installé.");
            System.exit(1);
        }
        
        // Lancer l'application
        launch(JavaFXLauncher.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("JavaFXLauncher.start() appelé");
        
        // Créer une instance de Home et lancer son start()
        Home home = new Home();
        try {
            home.start(primaryStage);
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }
} 