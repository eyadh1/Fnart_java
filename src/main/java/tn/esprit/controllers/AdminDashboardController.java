package tn.esprit.controllers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import tn.esprit.interfaces.ParentControllerAware;
import tn.esprit.models.Artwork;
import tn.esprit.models.Commande;
import tn.esprit.services.serviceartwork;
import tn.esprit.services.ServiceCommande;
import tn.esprit.controllers.AjoutCommandeController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Comparator;
import java.util.Date;

public class AdminDashboardController extends listartwork implements Initializable, ParentControllerAware {

    // Constants
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";
    private File selectedFile;

    // Profile Components
    @FXML private VBox profileContainer;
    @FXML private ImageView profileImage;
    @FXML private AnchorPane profilePanel;
    @FXML private ImageView profileImageLarge;
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;

    // Artwork ListView Component
    @FXML private ListView<Artwork> artworksListView;

    // Order ListView Component
    @FXML private TableView<Commande> ordersTableView;
    @FXML private TableColumn<Commande, Integer> orderIdColumn;
    @FXML private TableColumn<Commande, String> orderCustomerColumn;
    @FXML private TableColumn<Commande, String> orderArtworkColumn;
    @FXML private TableColumn<Commande, Date> orderDateColumn;
    @FXML private TableColumn<Commande, String> orderStatusColumn;
    @FXML private TableColumn<Commande, Double> orderTotalColumn;

    // Filter Components
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;

    // Data
    private ObservableList<Artwork> artworkList;
    private ObservableList<Commande> orderList;
    private FilteredList<Artwork> filteredArtworkData;
    private final ServiceCommande orderService = new ServiceCommande();
    private Object parentController;
    private serviceartwork artworkService;

    public AdminDashboardController() {
        artworkService = new serviceartwork();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Initialize directories
            initDirectories();

            // Load admin profile info
            initializeProfile();

            // Setup artwork list view
            setupListView();

            // Setup order list view
            setupOrderListView();

            // Load data
            loadArtworkData();
            loadOrderData();
            setupSearch();
            initializeSorting();

            // Set up profile panel toggle
            setupProfilePanel();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Load the login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Art Therapy - Login");
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to logout: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editprofile.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open profile editor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            artworkService.update(selectedArtwork);
            refreshTableView();
            showAlert("Success", "Artwork approved successfully.");
        } else {
            showAlert("Warning", "Please select an artwork to approve.");
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            artworkService.update(selectedArtwork);
            refreshTableView();
            showAlert("Success", "Artwork rejected successfully.");
        } else {
            showAlert("Warning", "Please select an artwork to reject.");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            editArtwork(selectedArtwork);
        } else {
            showAlert("Warning", "Please select an artwork to update.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Confirmation");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete '" + selectedArtwork.getTitre() + "'?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    artworkService.delete(selectedArtwork);
                    artworkList.remove(selectedArtwork);
                    showAlert("Success", "Artwork deleted successfully.");
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete artwork: " + e.getMessage());
                }
            }
        } else {
            showAlert("Warning", "Please select an artwork to delete.");
        }
    }

    // Helper method to refresh the table view
    private void refreshTableView() {
        artworkList.setAll(artworkService.getAll());
    }

    public void refreshArtworkTable() {
        loadArtworkData();
        showAlert("Refresh", "Artwork table refreshed successfully.");
    }

    private void initDirectories() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    private void initializeProfile() {
        // Set default admin info
        adminNameLabel.setText("Admin User");
        adminEmailLabel.setText("admin@arttherapy.com");

        // You can load admin info from database/config later
    }

    private void setupProfilePanel() {
        // Initially hidden
        profilePanel.setVisible(false);
        profilePanel.setManaged(false);
    }

    private void setupListView() {
        artworksListView.setCellFactory(param -> new ListCell<Artwork>() {
            private HBox content;
            private ImageView imageView;
            private VBox details;
            private Label title;
            private Label artist;
            private Label price;
            private Label description;

            {
                content = new HBox(10);
                content.setPadding(new Insets(10));
                content.setPrefWidth(param.getWidth() - 20);

                imageView = new ImageView();
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);

                details = new VBox(5);
                title = new Label();
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                artist = new Label();
                price = new Label();
                description = new Label();
                description.setWrapText(true);
                description.setMaxWidth(400);

                details.getChildren().addAll(title, artist, price, description);
                content.getChildren().addAll(imageView, details);
                HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
            }

            @Override
            protected void updateItem(Artwork artwork, boolean empty) {
                super.updateItem(artwork, empty);

                if (empty || artwork == null) {
                    setGraphic(null);
                } else {
                    title.setText(artwork.getTitre());
                    artist.setText("Artist: " + artwork.getArtistenom());
                    price.setText(String.format("Price: $%d", artwork.getPrix()));
                    description.setText(artwork.getDescription());

                    // Load image
                    try {
                        String fullPath = "C:/xampp/htdocs/artwork_images/" + artwork.getImage();
                        File file = new File(fullPath);
                        if (file.exists()) {
                            Image image = new Image(file.toURI().toString());
                            imageView.setImage(image);
                        } else {
                            imageView.setImage(null);
                        }
                    } catch (Exception e) {
                        imageView.setImage(null);
                    }

                    setGraphic(content);
                }
            }
        });
    }

    private void createOrder(Artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutCommande.fxml"));
            Parent root = loader.load();

            AjoutCommandeController controller = loader.getController();
            controller.initData(artwork);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Order - " + artwork.getTitre());
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open order form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editOrder(Commande order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommande.fxml"));
            Parent root = loader.load();

            modifiercommandeController controller = loader.getController();
            controller.setCommande(order);
            
            // Add reference back to parent controller if needed
            if (controller instanceof ParentControllerAware) {
                ((ParentControllerAware) controller).setParentController(this);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Order - #" + order.getId());
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open order editor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteOrder(Commande order) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete Order #" + order.getId() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                orderService.delete(order);
                orderList.remove(order);
                showAlert("Success", "Order deleted successfully.");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete order: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setupOrderListView() {
        // Set up the columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        orderArtworkColumn.setCellValueFactory(cellData -> {
            Artwork artwork = cellData.getValue().getArtwork();
            return new SimpleStringProperty(artwork != null ? artwork.getTitre() : "N/A");
        });
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totale"));
    }

    private void loadOrderData() {
        orderList = FXCollections.observableArrayList(orderService.getAll());
        ordersTableView.setItems(orderList);
    }

    private void loadArtworkData() {
        try {
            artworkList = FXCollections.observableArrayList(artworkService.getAll());
            filteredArtworkData = new FilteredList<>(artworkList, p -> true);
            artworksListView.setItems(filteredArtworkData);
        } catch (Exception e) {
            showAlert("Error", "Failed to load artwork data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                artworksListView.setItems(artworkList);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                java.util.List<Artwork> filteredList = artworkList.stream()
                        .filter(artwork -> artwork.getTitre().toLowerCase().contains(lowerCaseFilter)
                                || artwork.getArtistenom().toLowerCase().contains(lowerCaseFilter)
                                || artwork.getDescription().toLowerCase().contains(lowerCaseFilter))
                        .toList();
                javafx.collections.ObservableList<Artwork> filteredObservableList = javafx.collections.FXCollections.observableArrayList(filteredList);
                artworksListView.setItems(filteredObservableList);
            }
        });
    }

    private void initializeSorting() {
        sortComboBox.getItems().addAll("Title", "Price (Low to High)", "Price (High to Low)", "Artist");
        sortComboBox.setOnAction(e -> handleSort());
    }

    @FXML
    private void handleSort() {
        String selectedSort = sortComboBox.getValue();
        if (selectedSort != null) {
            ObservableList<Artwork> items = artworksListView.getItems();
            java.util.List<Artwork> sortedList = null;
            switch (selectedSort) {
                case "Title":
                    sortedList = items.stream()
                        .sorted(Comparator.comparing(Artwork::getTitre))
                        .toList();
                    break;
                case "Price (Low to High)":
                    sortedList = items.stream()
                        .sorted(Comparator.comparing(Artwork::getPrix))
                        .toList();
                    break;
                case "Price (High to Low)":
                    sortedList = items.stream()
                        .sorted(Comparator.comparing(Artwork::getPrix).reversed())
                        .toList();
                    break;
                case "Artist":
                    sortedList = items.stream()
                        .sorted(Comparator.comparing(Artwork::getArtistenom))
                        .toList();
                    break;
            }
            if (sortedList != null) {
                javafx.collections.ObservableList<Artwork> sortedObservableList = javafx.collections.FXCollections.observableArrayList(sortedList);
                artworksListView.setItems(sortedObservableList);
            }
        }
    }

    private void editArtwork(Artwork artwork) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierartwork.fxml"));
            Parent root = loader.load();

            modifierartworkController controller = loader.getController();
            controller.setArtwork(artwork);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Artwork - " + artwork.getTitre());
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open editor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void approveArtwork(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            try {
                artworkService.approveArtwork(selectedArtwork.getId());
                selectedArtwork.setApproved(true);
                artworksListView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'œuvre d'art a été approuvée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'approbation de l'œuvre d'art.");
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une œuvre d'art à approuver.");
        }
    }

    @FXML
    private void disapproveArtwork(ActionEvent event) {
        Artwork selectedArtwork = artworksListView.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            try {
                artworkService.disapproveArtwork(selectedArtwork.getId());
                selectedArtwork.setApproved(false);
                artworksListView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'approbation de l'œuvre d'art a été retirée.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du retrait de l'approbation.");
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une œuvre d'art.");
        }
    }

    @Override
    public void refreshTable() {
        loadArtworkData();
    }

    @FXML
    private void toggleProfilePanel() {
        boolean isVisible = profilePanel.isVisible();
        profilePanel.setVisible(!isVisible);
        profilePanel.setManaged(!isVisible);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // Search is already handled by the text property listener
    }

    @FXML
    private void filterByPrice(ActionEvent event) {
        try {
            double minPrix = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
            double maxPrix = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

            filteredArtworkData.setPredicate(artwork -> {
                double prix = artwork.getPrix();
                return prix >= minPrix && prix <= maxPrix;
            });
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for prices.");
        }
    }

    @FXML
    private void showAddForm(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ajouterartwork.fxml");
            if (url == null) {
                throw new IOException("Cannot find FXML file: /ajouterartwork.fxml");
            }
            
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            
            ajouterartworkController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            // Add CSS
            scene.getStylesheets().add(getClass().getResource("/styles/ajouterartwork.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("Add New Artwork");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open add form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportToPDF(ActionEvent event) {
        // Existing method exporting all artworks - no change here
    }

    // Removed exportSelectedArtworkToPDF method as per user request to undo changes

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    @FXML
    private void handleRefresh() {
        // Implement logic to refresh the dashboard data
        loadArtworkData(); // Example: Reload artwork data
        showAlert("Refresh", "Dashboard refreshed successfully.");
    }

    @Override
    public void setParentController(Object parentController) {
        this.parentController = parentController;
    }

    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshOrderTable() {
        loadOrderData();
    }

    @FXML
    private void handleUpdateOrder(ActionEvent event) {
        Commande selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            try {
                // Load the update order form
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommande.fxml"));
                Parent root = loader.load();

                // Get the controller and set the order to update
                modifiercommandeController controller = loader.getController();
                controller.setCommande(selectedOrder);
                controller.setParentController(this);

                // Show the update form in a new stage
                Stage stage = new Stage();
                stage.setTitle("Update Order");
                stage.setScene(new Scene(root));
                stage.show();

                // Refresh the order list after update
                stage.setOnHidden(e -> refreshOrderTable());
            } catch (IOException e) {
                showAlert("Error", "Failed to load update form: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Warning", "Please select an order to update.");
        }
    }

    @FXML
    private void handleDeleteOrder() {
        Commande selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            deleteOrder(selectedOrder);
        } else {
            showAlert("Warning", "Please select an order to delete.");
        }
    }
}