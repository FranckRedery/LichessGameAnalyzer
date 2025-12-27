package analysis;

import domain.GameError;
import domain.RawMoveEvaluation;

public interface ErrorClassifier {

    GameError classify(RawMoveEvaluation evaluation);
}
