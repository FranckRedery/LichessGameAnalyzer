package parser;

import analysis.ErrorClassifier;
import domain.AnalysisResult;
import domain.GameError;
import domain.RawMoveEvaluation;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LichessAnalysisParser {

    private final ErrorClassifier classifier;

    public LichessAnalysisParser(ErrorClassifier classifier) {
        this.classifier = classifier;
    }

    public AnalysisResult parse(String json) {

        JSONObject root = new JSONObject(json);
        JSONArray evals = root.getJSONArray("evals");

        List<GameError> errors = new ArrayList<>();

        for (int i = 0; i < evals.length(); i++) {
            JSONObject e = evals.getJSONObject(i);

            RawMoveEvaluation raw = new RawMoveEvaluation(
                    e.getInt("ply"),
                    e.getDouble("cpLoss")
            );

            errors.add(classifier.classify(raw));
        }

        return new AnalysisResult(errors);
    }
}


