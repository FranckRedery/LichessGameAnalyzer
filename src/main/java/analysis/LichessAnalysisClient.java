package analysis;

import domain.AnalysisResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class LichessAnalysisClient {
    private static final String API_URL = "https://lichess.org/api/cloud-eval";
    private static final String TOKEN = System.getenv("LICHESS_TOKEN");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AnalysisResult analyzeGame(String pgn) {
        try {
            if (TOKEN == null || TOKEN.isEmpty()) {
                throw new RuntimeException("Lichess API token is not set in environment variables.");
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString("pgn=" + java.net.URLEncoder.encode(pgn, StandardCharsets.UTF_8)))
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
