package fetch;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class LichessFetcher {

    private final HttpClient httpClient;

    public LichessFetcher() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String fetchGamesByUser(String username) throws Exception {
        String url = "https://lichess.org/api/games/user/" + username + "?max=5&pgnInJson=true";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/x-ndjson") // formato consigliato da Lichess API
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to fetch games: " + response.statusCode());
        }
    }
}