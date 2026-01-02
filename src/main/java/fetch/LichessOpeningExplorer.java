package fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.OpeningResponse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LichessOpeningExplorer {

    private static final String OPENING_URL = "https://explorer.lichess.ovh/masters";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<OpeningResponse> getOpeningFromFen(String fen) {

        try {
            String encodedFen = URLEncoder.encode(fen, StandardCharsets.UTF_8);
            String url = OPENING_URL + "?fen=" + encodedFen;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || response.body().isBlank()) {
                return Optional.empty();
            }

            OpeningResponse opening = mapper.readValue(response.body(), OpeningResponse.class);

            return opening.opening() != null ? Optional.of(opening) : Optional.empty();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isInOpeningTheory(String fen) {
        return getOpeningFromFen(fen).isPresent();
    }
}
