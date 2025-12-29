package fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.OpeningExplorerResponse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LichessOpeningExplorer {

    private static final String BASE_URL = "https://explorer.lichess.ovh/masters";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public boolean isInOpeningTheory(String fen) {
        try {
            String encodedFen = URLEncoder.encode(fen, StandardCharsets.UTF_8);
            String url = BASE_URL + "?fen=" + encodedFen + "&moves=8";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return false;
            }

            OpeningExplorerResponse data = mapper.readValue(response.body(), OpeningExplorerResponse.class);

            return data.moves != null && !data.moves.isEmpty();

        } catch (Exception e) {
            return false;
        }
    }
}
