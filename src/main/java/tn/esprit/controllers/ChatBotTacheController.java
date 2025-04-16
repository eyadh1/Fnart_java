package tn.esprit.controllers;


import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatBotTacheController {
    private final Map<String, String> keywordResponses;

    @FXML
    public VBox chatContainer;
    @FXML
    public TextArea chatArea;
    @FXML
    public TextField userInput;


    public ChatBotTacheController() {
        keywordResponses = new HashMap<>();
        // Greetings
        keywordResponses.put("hi", "Hello!");
        keywordResponses.put("hello", "Hi there!");
        keywordResponses.put("hey", "Hey!");
        keywordResponses.put("howdy", "Howdy!");
        keywordResponses.put("greetings", "Greetings!");
        keywordResponses.put("good morning", "Good morning!");
        keywordResponses.put("good afternoon", "Good afternoon!");
        keywordResponses.put("good evening", "Good evening!");
        keywordResponses.put("good day", "Good day!");
        keywordResponses.put("morning", "Good morning!");
        keywordResponses.put("afternoon", "Good afternoon!");
        keywordResponses.put("evening", "Good evening!");
        keywordResponses.put("night", "Good night!");
        keywordResponses.put("yo", "Yo!");
        keywordResponses.put("sup", "What's up?");
        keywordResponses.put("wtf", "WTF, Oh shit!");

        keywordResponses.put("what's up", "Not much, you?");
        keywordResponses.put("how's it going", "It's going well, thanks!");
        keywordResponses.put("how are you", "I'm just a bot, but I'm doing well, thanks for asking!");
        // Date and time
        keywordResponses.put("time temp wa9t wakt heure hours minutes d9aye9", "The current time is " + LocalTime.now());
        keywordResponses.put("date", "Today's date is " + LocalDate.now());
        keywordResponses.put("day nhar lyoum", "Today is " + LocalDate.now().getDayOfWeek().toString());
        keywordResponses.put("tomorrow ghodwa", "Tomorrow's date is " + LocalDate.now().plusDays(1));
        keywordResponses.put("yesterday berah emes", "Yesterday's date was " + LocalDate.now().minusDays(1));
        keywordResponses.put("weekday", "Today is a weekday.");
        keywordResponses.put("weekend", "Today is a weekend.");
        keywordResponses.put("next week jom3a jeya", "Next week starts on " + LocalDate.now().plusWeeks(1).getDayOfWeek().toString());
        keywordResponses.put("last week jom3a fetet", "Last week started on " + LocalDate.now().minusWeeks(1).getDayOfWeek().toString());
        keywordResponses.put("next month chhar jey", "Next month is " + LocalDate.now().plusMonths(1).getMonth().toString());
        keywordResponses.put("last month chhar fet", "Last month was " + LocalDate.now().minusMonths(1).getMonth().toString());
        keywordResponses.put("next year 3am jey", "Next year is " + LocalDate.now().plusYears(1).getYear());
        keywordResponses.put("last year 3amnewel", "Last year was " + LocalDate.now().minusYears(1).getYear());
        keywordResponses.put("holiday", "Today is a holiday!");
        keywordResponses.put("workday", "Today is a workday.");
        // Current weather
        keywordResponses.put("weather ta9s", "The current weather is sunny."); // Replace with actual weather API call
        // Job-related queries
        keywordResponses.put("job 5idma travail", "Sorry, I'm just a chatbot and don't have job listings."); // Replace with actual job API call
        // Animal-related queries
        keywordResponses.put("animal 7ayawen", "I love animals too!"); // Respond to general animal queries
        keywordResponses.put("dog kalb", "Dogs are great companions!"); // Respond to specific animal queries
        keywordResponses.put("cat katous", "Cats are independent creatures!");
        // Bad words recognition
        keywordResponses.put("badword klem mirzi", "Please be polite!"); // Example of recognizing bad words
        //bot questions
        keywordResponses.put("how old are you kdeh omrik", "I'm just a computer program, so I don't have an age.");
        keywordResponses.put("where are you from mnin enty", "I exist in the digital world, so I don't have a physical location.");
        keywordResponses.put("what is your purpose chnia feyidtik f denya", "My purpose is to assist you with tasks and provide information.");
        keywordResponses.put("do you have siblings aandek sghar", "No, I'm a standalone entity.");
        keywordResponses.put("what do you like to do chno t7ib taamel", "I enjoy helping users and learning new things!");
        keywordResponses.put("who are you chknk tqui toi", "I'm a  chatbot, to assist you with tasks and provide information.");
        keywordResponses.put("can you help me kifeh nijim n3awnek", "Of course, I'm here to help. What do you need assistance with?");
        keywordResponses.put("what can you do chno tijim taamel", "I can answer questions, provide information, and assist you with various tasks.");
        keywordResponses.put("tell me a joke nokta", "Why don't scientists trust atoms? Because they make up everything!");
        keywordResponses.put("what is the meaning of life ma3na hayet", "The meaning of life is subjective and can vary from person to person.");
        keywordResponses.put("tell me a fact het fact", "Did you know that the shortest war in history lasted only 38 minutes?");
        keywordResponses.put("are you a human enty bachar ", "No, I'm a chatbot powered by artificial intelligence.");
        keywordResponses.put("do you like movies aflem filem filme", "I don't have personal preferences, but I can provide information about movies.");
        keywordResponses.put("what is your favorite color couleur alwen ", "As a chatbot, I don't have the ability to perceive colors.");
        keywordResponses.put("do you dream ti7lim", "No, I don't have the ability to dream.");
        keywordResponses.put("what is your favorite food meka mofathla", "I don't have preferences for food since I'm not capable of eating.");
        keywordResponses.put("tell me something interesting ahki hja tisla7 interessante", "The world's largest desert is not the Sahara, but Antarctica.");
        keywordResponses.put("are you sentient enty monji", "No, I'm not sentient. I'm a computer program designed to respond to input.");
        keywordResponses.put("what is the capital of France capital mtaa fransa", "The capital of France is Paris.");
        keywordResponses.put("what is the population of China", "As of the latest data, the population of China is over 1.4 billion people.");
        keywordResponses.put("how do you  kifeh ti5dim", "I work by processing input from users and providing predefined responses based on keywords.");
        keywordResponses.put("what languages do you speak chnia lo8tik", "I can communicate in multiple languages, including English, French, and Arabic.");
        keywordResponses.put("me ena", "you are a great person.");

        keywordResponses.put("chbik", "chbini");
        keywordResponses.put("salem", "wa alaykom salem");
        keywordResponses.put("chtaaml", "nvalid f PI");
        keywordResponses.put("aloo", "winek sahbi");

        keywordResponses.put("waa", "waywa");
        keywordResponses.put("ai", "artificial intelligence");

        keywordResponses.put("java", "Java est un langage de programmation de haut niveau orienté objet créé par James Gosling et Patrick Naughton, employés de Sun Microsystems, avec le soutien de Bill Joy, présenté officiellement le 23 mai 1995 au SunWorld. La société Sun est rachetée en 2009 par la société Oracle qui détient et maintient désormais Java. ");
        keywordResponses.put("esprit", "se former autrement");
        keywordResponses.put("haha hhhh", "ith7ak saha lik");
        keywordResponses.put("mama", "mia");


        keywordResponses.put("cv ?", "Hamdoullah");
        keywordResponses.put("blablabla", "yizitchi m bleda");

        keywordResponses.put("salut", "Salut!");
        keywordResponses.put("bonjour", "Bonjour!");
        keywordResponses.put("coucou", "Coucou!");
        keywordResponses.put("ça va", "Ça va bien, et toi?");
        keywordResponses.put("comment ça va", "Ça va bien, et toi?");
        keywordResponses.put("ça roule", "Ça roule!");
        keywordResponses.put("quoi de neuf", "Pas grand chose, et toi?");
        keywordResponses.put("qui es-tu", "Je suis un chatbot conçu pour vous aider avec les tâches et fournir des informations.");
        keywordResponses.put("peux-tu m'aider", "Bien sûr, je suis là pour vous aider. De quoi avez-vous besoin d'aide?");
        keywordResponses.put("que peux-tu faire", "Je peux répondre aux questions, fournir des informations et vous aider dans diverses tâches.");
        keywordResponses.put("raconte-moi une blague", "Pourquoi les plongeurs plongent-ils toujours en arrière et jamais en avant? Parce que sinon ils tombent dans le bateau!");
        keywordResponses.put("quelle est la capitale de la France", "La capitale de la France est Paris.");
        keywordResponses.put("quelle est la population de la Chine", "Selon les dernières données, la population de la Chine est de plus de 1,4 milliard de personnes.");
        keywordResponses.put("comment fonctionnes-tu", "Je fonctionne en traitant les entrées des utilisateurs et en fournissant des réponses prédéfinies basées sur des mots clés.");
        keywordResponses.put("quelles langues parles-tu", "Je peux communiquer dans plusieurs langues, y compris l'anglais, le français et l'arabe.");
        keywordResponses.put("moi", "vous êtes une personne géniale.");


        keywordResponses.put("fine nikel mrigel", "great, how can i help");
        keywordResponses.put("bb", "hobi rak");
        keywordResponses.put("m3alem rak yre the bos t le patron", "thank you ya rayes");


    }

    public void processInput(ActionEvent actionEvent) {
        String inputText = userInput.getText();
        chatArea.appendText("You: " + inputText + "\n");

        // Simulate bot thinking with a delay
        Task<Void> thinkingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000); // Adjust the delay time as needed
                return null;
            }
        };

        thinkingTask.setOnSucceeded(event -> {
            String response = guessResponse(inputText);
            chatArea.appendText("chatbot: " + response + "\n");
            userInput.clear();
        });

        new Thread(thinkingTask).start();
    }

    private String guessResponse(String input) {
        double bestSimilarity = 0.0;
        String bestResponse = null;

        // Loop through each keyword and calculate similarity
        for (Map.Entry<String, String> entry : keywordResponses.entrySet()) {
            String keyword = entry.getKey();
            double similarity = calculateSimilarity(input, keyword);

            // Update the best response if similarity exceeds current best
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestResponse = entry.getValue();
            }
        }

        // Return the best response regardless of similarity
        return bestResponse;
    }

    private double calculateSimilarity(String input, String keyword) {
        // Tokenize the input and keyword
        String[] inputTokens = input.split("\\s+");
        String[] keywordTokens = keyword.split("\\s+");

        // Calculate similarity using different metrics
        CosineDistance cosineDistance = new CosineDistance();
        JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        // Calculate similarities using different metrics
        double cosineSim = 1 - cosineDistance.apply(input, keyword);
        double jaccardSim = jaccardSimilarity.apply(input, keyword);
        double levenshteinSim = 1.0 / (1.0 + levenshteinDistance.apply(input, keyword));

        // Return the average similarity
        return (cosineSim + jaccardSim + levenshteinSim) / 3.0;
    }

    @FXML
    public void clearConversation(ActionEvent actionEvent) {
        chatArea.clear();
    }

    public void back(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherForum.fxml")); // Adjust the path if necessary

            // Set the new scene
            Stage stage = (Stage) chatContainer.getScene().getWindow();
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
}
