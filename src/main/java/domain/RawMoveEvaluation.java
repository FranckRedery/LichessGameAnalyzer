package domain;

import com.github.bhlangonijr.chesslib.Side;
import domain.enums.GamePhase;

public record RawMoveEvaluation(String gameId, Side playerColor, int moveNumber, String uciMove, String sanMove,
                                double evalBefore, double evalAfter, double bestEval, String bestMoveUci,
                                boolean forced, int legalMovesCount, int materialBalance, int materialDelta,
                                String fenBefore, String fenAfter, double cpLoss, double relativeCpLoss,
                                GamePhase phase, Side sideToMoveAfter, boolean capture, boolean check,
                                boolean promotion, boolean createsMateThreat, boolean inOpeningTheory,
                                String openingName, String openingEco) {


}
