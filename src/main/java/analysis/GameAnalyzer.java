package analysis;

import domain.AnalysisResult;

import java.util.ArrayList;
import java.util.List;

public class GameAnalyzer {
    private final LichessAnalysisClient analysisClient;

    public GameAnalyzer(LichessAnalysisClient client) {
        this.analysisClient = client;
    }

    public List<AnalysisResult> analyzeGames(List<String> pgnList) {
        List<AnalysisResult> results = new ArrayList<>();
        for (String pgn : pgnList) {
            results.add(analysisClient.analyzeGame(pgn));
        }
        return results;
    }
}
