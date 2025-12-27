package analysis;

import com.github.bhlangonijr.chesslib.Side;
import domain.AnalysisResult;
import domain.GameError;
import domain.RawMoveEvaluation;

import java.util.ArrayList;
import java.util.List;

public class GameAnalysisService {

    private final StockfishClient stockfishClient;
    private final SimpleErrorClassifier errorClassifier;

    public GameAnalysisService(StockfishClient stockfishClient, SimpleErrorClassifier errorClassifier) {
        this.stockfishClient = stockfishClient;
        this.errorClassifier = errorClassifier;
    }

    public AnalysisResult analyzeGame(String pgn, int depth, Side playerSide) throws Exception {
        List<RawMoveEvaluation> rawEvaluations = stockfishClient.analyzePGN(pgn, depth, playerSide);

        List<GameError> errors = new ArrayList<>();
        for (RawMoveEvaluation raw : rawEvaluations) {
            errors.add(errorClassifier.classify(raw));
        }

        return new AnalysisResult(errors);
    }
}