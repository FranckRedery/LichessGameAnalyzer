package fetch;

import domain.LichessGame;
import parser.PGNParser;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LichessFetcher {

    private final HttpClient httpClient;
    private final String BASE_URL = "https://lichess.org/api/games/user/";

    public LichessFetcher() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<LichessGame> fetchGamesByUser(String username, int maxGames) throws Exception {
        var url = BASE_URL + username + "?max=" + maxGames + "&pgnInJson=true";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/x-ndjson")
                .GET()
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch games: " + response.statusCode());
        }

        return PGNParser.parseNdjson(response.body());
    }

    public List<LichessGame> fetchGamesByDate(String username, LocalDate fromDate, LocalDate toDate) throws Exception {
        var fromTimestamp = fromDate.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);
        var toTimestamp = toDate.plusDays(1).atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC) - 1;
        var url = BASE_URL + username + "?since=" + (fromTimestamp * 1000) + "&until=" + (toTimestamp * 1000) + "&pgnInJson=true";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/x-ndjson")
                .GET()
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return PGNParser.parseNdjson(response.body());
        } else {
            throw new RuntimeException("Failed to fetch games: " + response.statusCode());
        }
    }

}