package analysis;

import domain.AnalysisResult;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONObject;
import parser.LichessAnalysisParser;


public class LichessAnalysisClient {

    private static final String API_URL = "https://lichess.org/api/cloud-eval";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String token;
    private final LichessAnalysisParser lichessAnalysisParser = new LichessAnalysisParser(new SimpleErrorClassifier());

    public LichessAnalysisClient(String token) {
        this.token = token;
    }

    public AnalysisResult analyzeGameRaw(String pgn) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "pgn=" + URLEncoder.encode(pgn, StandardCharsets.UTF_8)))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Lichess API error: " + response.body());
            }

            return lichessAnalysisParser.parse(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Lichess API", e);
        }
    }
}
