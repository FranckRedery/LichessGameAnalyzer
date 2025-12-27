package analysis;

import domain.GameError;
import domain.RawMoveEvaluation;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

public class SimpleErrorClassifier implements ErrorClassifier {

    private static final double MIN_ERROR_CP = 20; // minimum centipawn loss to consider an error
    private static final double INACCURACY_THRESHOLD = 50;
    private static final double MISTAKE_THRESHOLD = 150;
    private static final double BLUNDER_THRESHOLD = 300;

    @Override
    public GameError classify(RawMoveEvaluation rawEval) {

        if (rawEval.getCentipawnLoss() < MIN_ERROR_CP) {
            return null;
        }

        ErrorSeverity severity = classifySeverity(rawEval.getCentipawnLoss());
        GamePhase phase = rawEval.getPhase();
        ErrorCategory category = classifyCategory(rawEval, severity);

        return new GameError(
                severity,
                category,
                phase,
                rawEval.getMoveNumber(),
                rawEval.getCentipawnLoss()
        );
    }

    private ErrorSeverity classifySeverity(double cpLoss) {
        if (cpLoss < INACCURACY_THRESHOLD) return ErrorSeverity.INACCURACY;
        if (cpLoss <= MISTAKE_THRESHOLD) return ErrorSeverity.MISTAKE;
        return ErrorSeverity.BLUNDER;
    }

    private ErrorCategory classifyCategory(RawMoveEvaluation eval, ErrorSeverity severity) {
        if (eval.getPhase() == GamePhase.OPENING) {
            return ErrorCategory.OPENING_KNOWLEDGE;
        }

        if (eval.isCapture() || eval.isCheck() || eval.isPromotion()) {
            return ErrorCategory.TACTICAL;
        }

        if (eval.getPhase() == GamePhase.ENDGAME || eval.getPhase() == GamePhase.MIDDLEGAME && eval.getMaterialBalance() < 1000) {
            return ErrorCategory.ENDGAME_TECHNIQUE;
        }

        // Distinguo tra strategico e positional
        if (severity == ErrorSeverity.MISTAKE || severity == ErrorSeverity.BLUNDER) {
            if (Math.abs(eval.getMaterialBalance()) < 500) {
                return ErrorCategory.STRATEGIC;
            } else {
                return ErrorCategory.POSITIONAL;
            }
        }

        // Default
        return ErrorCategory.STRATEGIC;
    }

}
