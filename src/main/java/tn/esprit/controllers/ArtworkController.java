package tn.esprit.controllers;

import tn.esprit.interfaces.ParentControllerAware;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ArtworkController implements Initializable, ParentControllerAware {
    private Object parentController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code here
    }
    
    @Override
    public void setParentController(Object parentController) {
        this.parentController = parentController;
    }
} 