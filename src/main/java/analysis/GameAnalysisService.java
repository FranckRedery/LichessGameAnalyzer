package analysis;

import com.github.bhlangonijr.chesslib.Side;
import domain.AnalysisResult;
import domain.GameError;
import domain.LichessGame;
import domain.RawMoveEvaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameAnalysisService {

    private final StockfishClient stockfishClient;
    private final AdvancedErrorClassifier advancedErrorClassifier;

    public GameAnalysisService(StockfishClient stockfishClient, AdvancedErrorClassifier errorClassifier) {
        this.stockfishClient = stockfishClient;
        this.advancedErrorClassifier = errorClassifier;
    }

    public AnalysisResult analyzeGame(LichessGame game, int depth, Side playerSide) throws Exception {
        List<RawMoveEvaluation> rawEvaluations = stockfishClient.analyzePGN(game, depth, playerSide);

        List<GameError> errors = new ArrayList<>();
        rawEvaluations.stream().map(advancedErrorClassifier::classify).filter(Objects::nonNull).forEach(errors::add);
        return new AnalysisResult(errors);
    }
}