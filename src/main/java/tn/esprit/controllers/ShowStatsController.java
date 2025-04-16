package tn.esprit.controllers;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import tn.esprit.models.Forum;
import tn.esprit.services.ServiceForum;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ShowStatsController implements Initializable {

    @FXML
    private AnchorPane Showstats;

    @FXML
    private Button goafficher;

    private ServiceForum serviceForum = new ServiceForum();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create and display the chart when the controller initializes
        createForumStatistics();
    }

    private void createForumStatistics() {
        try {
            // Create the dataset from forum data
            DefaultCategoryDataset dataset = createDataset();

            // Create the bar chart
            JFreeChart chart = ChartFactory.createBarChart(
                    "Forum Statistics by Category",  // Chart title
                    "Category",                      // X-Axis label
                    "Number of Forums",              // Y-Axis label
                    dataset,                         // Dataset
                    PlotOrientation.VERTICAL,        // Plot orientation
                    true,                            // Include legend
                    true,                            // Include tooltips
                    false                            // URLs
            );

            // Create a panel containing the chart
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(400, 300));

            // Create a SwingNode to host the Swing-based chart in JavaFX
            SwingNode swingNode = new SwingNode();
            swingNode.setContent(chartPanel);

            // Add the chart to the AnchorPane
            Showstats.getChildren().clear(); // Clear any existing content (like the gif)
            Showstats.getChildren().add(swingNode);

            // Set the position of the chart
            AnchorPane.setTopAnchor(swingNode, 0.0);
            AnchorPane.setBottomAnchor(swingNode, 0.0);
            AnchorPane.setLeftAnchor(swingNode, 0.0);
            AnchorPane.setRightAnchor(swingNode, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create statistics chart: " + e.getMessage());
        }
    }

    private DefaultCategoryDataset createDataset() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Get all forums
        List<Forum> forums = serviceForum.getAll();

        // Count forums by category
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (Forum forum : forums) {
            String category = forum.getCategorie_f();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        // Add data to dataset
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Forums", entry.getKey());
        }

        return dataset;
    }

    public void goafficherAction(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherForum.fxml")); // Adjust the path if necessary

            // Set the new scene
            Stage stage = (Stage) Showstats.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Erreur lors du chargement de la vue.");
            alert.setTitle("Erreur");
            alert.show();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}