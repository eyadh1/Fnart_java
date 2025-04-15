module java_pi{
    requires javafx.controls;
requires java.sql;
requires javafx.fxml;
    requires java.desktop;
    requires layout;
    requires kernel;

    exports tn.esprit.controllers;
    exports tn.esprit.service;
    exports tn.esprit.models;
    exports tn.esprit.test;
    opens tn.esprit.controllers to javafx.fxml;



}