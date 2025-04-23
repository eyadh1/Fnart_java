package tn.esprit.interfaces;

/**
 * Interface for controllers that need to maintain a reference to their parent controller.
 */
public interface ParentControllerAware {
    void setParentController(Object parentController);
} 