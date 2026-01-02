package app;

import analysis.AdvancedErrorClassifier;
import analysis.GameAnalysisService;
import analysis.StockfishClient;
import com.github.bhlangonijr.chesslib.Side;
import domain.GameError;
import domain.LichessGame;
import fetch.LichessFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LichessAnalyzerApp {

    private static final String USERNAME = "FranckReda96";

    static void main() {

        LichessFetcher fetcher = new LichessFetcher();

        try {
            List<LichessGame> games = fetcher.fetchGamesByUser(USERNAME, 10);
            System.out.println("Fetched " + games.size() + " games");

            List<GameError> allErrors = new ArrayList<>();
            ExecutorService executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() - 1));
            List<Future<?>> futures = new ArrayList<>();

            for (LichessGame game : games) {

                futures.add(executor.submit(() -> {

                    StockfishClient stockfish = new StockfishClient();

                    try {
                        stockfish.start();

                        Side side = determinePlayerSide(game, USERNAME);
                        if (side == null) {
                            System.out.println("Skipping game: user not found");
                            return;
                        }

                        GameAnalysisService analysisService = new GameAnalysisService(stockfish, new AdvancedErrorClassifier());

                        var analysis = analysisService.analyzeGame(game, 17, side);

                        synchronized (allErrors) {
                            allErrors.addAll(analysis.errors());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        stockfish.stop();
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }

            executor.shutdown();

            GameErrorReportGenerator reportGenerator = new GameErrorReportGenerator(allErrors);
            reportGenerator.generateAndOpenHtmlReport();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Side determinePlayerSide(LichessGame game, String username) {
        if (username.equalsIgnoreCase(game.white())) return Side.WHITE;
        if (username.equalsIgnoreCase(game.black())) return Side.BLACK;
        return null;
    }
}
