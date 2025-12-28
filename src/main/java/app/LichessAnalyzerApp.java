package app;

import analysis.AdvancedErrorClassifier;
import analysis.GameAnalysisService;
import analysis.StockfishClient;
import com.github.bhlangonijr.chesslib.Side;
import domain.LichessGame;
import fetch.LichessFetcher;

import java.util.List;

public class LichessAnalyzerApp {

    private static final String USERNAME = "FranckReda96";

    static void main() {
        LichessFetcher fetcher = new LichessFetcher();
        StockfishClient stockfish = new StockfishClient();

        try {
            stockfish.start();

            List<LichessGame> games = fetcher.fetchGamesByUser(USERNAME, 100);
            System.out.println("Fetched " + games.size() + " games");

            GameAnalysisService analysisService = new GameAnalysisService(stockfish, new AdvancedErrorClassifier());

            for (LichessGame game : games) {

                Side side = determinePlayerSide(game, USERNAME);

                if (side == null) {
                    System.out.println("Skipping game: user not found");
                    continue;
                }

                var analysis = analysisService.analyzeGame(game, 17, side);

                System.out.println("Game " + game.getGameId() + " | Errors found: " + analysis.getErrors());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stockfish.stop();
        }
    }

    private static Side determinePlayerSide(LichessGame game, String username) {
        if (username.equalsIgnoreCase(game.getWhite())) return Side.WHITE;
        if (username.equalsIgnoreCase(game.getBlack())) return Side.BLACK;
        return null;
    }

}