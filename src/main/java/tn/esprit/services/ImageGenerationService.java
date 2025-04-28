package tn.esprit.services; // Assurez-vous que le package est correct

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class ImageGenerationService {

    // Utiliser le client HTTP intégré de Java 11+
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(20)) // Timeout de connexion
            .build(); // [1]

    private final ObjectMapper objectMapper = new ObjectMapper(); // Pour la manipulation JSON [2, 3, 4, 5]

    // Récupère la clé API et l'URL de l'API Stable Diffusion depuis application.properties
    private String getStabilityApiKey() {
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                String apiKey = props.getProperty("stability.api.key");
                if (apiKey != null && !apiKey.trim().isEmpty()) {
                    return apiKey;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de la clé API Stability.ai : " + e.getMessage());
        }
        return null;
    }

    private String getStabilityApiUrl() {
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                String url = props.getProperty("stable.diffusion.api.url");
                if (url != null && !url.trim().isEmpty()) {
                    return url;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de l'URL API Stability.ai : " + e.getMessage());
        }
        return null;
    }

    /**
     * Appelle l'API Stable Diffusion de Stability.ai pour générer une image à partir d'un prompt.
     * @param prompt Le texte décrivant l'image à générer.
     * @return Un CompletableFuture contenant l'URL de l'image générée ou les données Base64.
     */
    public CompletableFuture<String> generateImageWithOptionsAsync(
            String prompt, String unusedApiKey, String unusedModel, String unusedQuality, String unusedStyle, String unusedSize, String unusedResponseFormat) {

        String apiKey = getStabilityApiKey();
        String apiUrl = getStabilityApiUrl();
        if (apiKey == null || apiUrl == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Clé API ou URL de l'API Stability.ai manquante."));
        }
        if (prompt == null || prompt.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Le prompt ne peut pas être vide."));
        }

        // Préparer le corps JSON pour l'API Stable Diffusion
        String jsonBody = String.format("""
        {
          \"text_prompts\": [
            {\"text\": \"%s\"}
          ],
          \"cfg_scale\": 7,
          \"clip_guidance_preset\": \"FAST_BLUE\",
          \"height\": 1024,
          \"width\": 1024,
          \"samples\": 1,
          \"steps\": 30
        }
        """, prompt.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(90))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    String responseBody = response.body();
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            // L'API Stable Diffusion retourne un tableau 'artifacts' avec des images en base64
                            JsonNode artifacts = rootNode.path("artifacts");
                            if (artifacts.isArray() && artifacts.size() > 0) {
                                String base64 = artifacts.get(0).path("base64").asText(null);
                                if (base64 != null) {
                                    return base64;
                                }
                            }
                            throw new IOException("Réponse API succès mais format invalide : pas d'image trouvée.");
                        } catch (IOException e) {
                            throw new RuntimeException("Erreur lors de l'analyse de la réponse JSON de l'API Stability.ai.", e);
                        }
                    } else {
                        String errorMessage = "Erreur API Stability.ai : " + statusCode + " - " + responseBody;
                        throw new RuntimeException(errorMessage);
                    }
                });
    }

    // Pour compatibilité ascendante
    public CompletableFuture<String> generateImageAsync(String prompt, String unusedApiKey) {
        return generateImageWithOptionsAsync(prompt, null, null, null, null, null, null);
    }
} 