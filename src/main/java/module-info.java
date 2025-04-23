module java_pi {
    requires javafx.controls;
    requires java.sql;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires jbcrypt;

    exports tn.esprit.controllers;
    exports tn.esprit.services;
    exports tn.esprit.models;
    exports tn.esprit.test;
    exports tn.esprit.interfaces;
    
    opens tn.esprit.controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;
}