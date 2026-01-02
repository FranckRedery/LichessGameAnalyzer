package domain;

import com.github.bhlangonijr.chesslib.Side;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

public record GameError(String gameId, int moveNumber, Side playerColor, ErrorSeverity severity, ErrorCategory category,
                        GamePhase phase, double centipawnLoss, double evalBefore, double evalAfter, double bestEval,
                        String playedMoveUci, String playedMoveSan, String bestMoveUci, String fenBefore,
                        String fenAfter, String openingName, String openingEco) {

    public static GameError from(RawMoveEvaluation eval,
                                 ErrorSeverity severity,
                                 ErrorCategory category) {

        return new GameError(
                eval.gameId(),
                eval.moveNumber(),
                eval.playerColor(),
                severity,
                category,
                eval.phase(),
                eval.cpLoss(),
                eval.evalBefore(),
                eval.evalAfter(),
                eval.bestEval(),
                eval.uciMove(),
                eval.sanMove(),
                eval.bestMoveUci(),
                eval.fenBefore(),
                eval.fenAfter(),
                eval.openingName(),
                eval.openingEco()
        );
    }
}
