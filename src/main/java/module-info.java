module java_pi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires java.sql;
    requires javafx.swing;
    opens tn.esprit.controllers to javafx.fxml;
    exports tn.esprit.controllers;
    exports tn.esprit.services;
}