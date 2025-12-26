package analysis;

import domain.AnalysisResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import org.json.JSONObject;

public class LichessAnalysisClient {
    private static final String API_URL = "https://lichess.org/api/cloud-eval";
    private static final String TOKEN = "YOUR_LICHESS_TOKEN"; // Inserisci qui il tuo token personale
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AnalysisResult analyzeGame(String pgn) {
        try {
            // Costruisci la richiesta POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString("pgn=" + java.net.URLEncoder.encode(pgn, java.nio.charset.StandardCharsets.UTF_8)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Parsing della risposta JSON
                JSONObject json = new JSONObject(response.body());
                int inaccuracies = json.optInt("inaccuracy", 0);
                int mistakes = json.optInt("mistake", 0);
                int blunders = json.optInt("blunder", 0);
                AnalysisResult result = new AnalysisResult();
                result.setInaccuracies(inaccuracies);
                result.setMistakes(mistakes);
                result.setBlunders(blunders);
                return result;
            } else {
                throw new RuntimeException("Lichess API error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze game: " + e.getMessage(), e);
        }
    }
}
