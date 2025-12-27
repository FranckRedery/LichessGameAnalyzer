package analysis;

import domain.GameError;
import domain.RawMoveEvaluation;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

public class SimpleErrorClassifier implements ErrorClassifier {

    @Override
    public GameError classify(RawMoveEvaluation rawEval) {
        ErrorSeverity severity = classifySeverity(rawEval.getCentipawnLoss());
        GamePhase phase = classifyPhase(rawEval.getMoveNumber());
        ErrorCategory category = classifyCategory(phase, rawEval.getCentipawnLoss());

        return new GameError(
                severity,
                category,
                phase,
                rawEval.getMoveNumber(),
                rawEval.getCentipawnLoss()
        );
    }

    private ErrorSeverity classifySeverity(double cpLoss) {
        if (cpLoss < 50) return ErrorSeverity.INACCURACY;
        if (cpLoss <= 100) return ErrorSeverity.MISTAKE;
        return ErrorSeverity.BLUNDER;
    }

    private GamePhase classifyPhase(int move) {
        if (move <= 20) return GamePhase.OPENING;
        if (move <= 50) return GamePhase.MIDDLEGAME;
        return GamePhase.ENDGAME;
    }

    private ErrorCategory classifyCategory(GamePhase phase, double cpLoss) {
        if (phase == GamePhase.OPENING) {
            return ErrorCategory.OPENING_KNOWLEDGE;
        }
        if (cpLoss > 300) {
            return ErrorCategory.TACTICAL;
        }
        return ErrorCategory.STRATEGIC;
    }
}

