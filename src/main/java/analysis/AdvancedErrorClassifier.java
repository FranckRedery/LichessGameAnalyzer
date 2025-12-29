package analysis;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import domain.GameError;
import domain.RawMoveEvaluation;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

import java.util.List;

import static com.github.bhlangonijr.chesslib.PieceType.*;

public class AdvancedErrorClassifier implements ErrorClassifier {

    private static final double INACCURACY_THRESHOLD = 50;
    private static final double MISTAKE_THRESHOLD = 100;
    private static final double BLUNDER_THRESHOLD = 300;

    @Override
    public GameError classify(RawMoveEvaluation eval) {

        double cpLoss = eval.getCentipawnLoss();

        if (cpLoss < INACCURACY_THRESHOLD) {
            return null;
        }

        ErrorSeverity severity = classifySeverity(cpLoss);
        ErrorCategory category = classifyCategory(eval, severity);

        return new GameError(
                severity,
                category,
                eval.getPhase(),
                eval.getMoveNumber(),
                cpLoss
        );
    }

    private ErrorSeverity classifySeverity(double cpLoss) {
        if (cpLoss < MISTAKE_THRESHOLD) return ErrorSeverity.INACCURACY;
        if (cpLoss < BLUNDER_THRESHOLD) return ErrorSeverity.MISTAKE;
        return ErrorSeverity.BLUNDER;
    }

    private ErrorCategory classifyCategory(RawMoveEvaluation eval, ErrorSeverity severity) {

        if (eval.getPhase() == GamePhase.OPENING &&
                eval.isInOpeningTheory() &&
                eval.getMoveNumber() <= 12) {
            return ErrorCategory.OPENING_KNOWLEDGE;
        }

        if (isTacticalError(eval)) {
            return ErrorCategory.TACTICAL;
        }

        if (eval.getPhase() == GamePhase.ENDGAME &&
                Math.abs(eval.getMaterialBalance()) <= 500) {
            return ErrorCategory.ENDGAME_TECHNIQUE;
        }

        if (severity != ErrorSeverity.INACCURACY) {
            if (Math.abs(eval.getMaterialBalance()) < 300) {
                return ErrorCategory.STRATEGIC;
            }
            return ErrorCategory.POSITIONAL;
        }

        return ErrorCategory.POSITIONAL;
    }

    /* =======================
       TACTICAL ANALYSIS
       ======================= */

    private boolean isTacticalError(RawMoveEvaluation eval) {

        if (eval.getCentipawnLoss() < 100) return false;

        if (eval.isCapture() || eval.isCheck() || eval.isPromotion()) {
            return true;
        }

        if (Math.abs(eval.getEvalBefore() - eval.getEvalAfter()) >= 150 &&
                Math.abs(eval.getMaterialBalance()) >= 200) {
            return true;
        }

        return hasTacticalPattern(eval);
    }

    private boolean hasTacticalPattern(RawMoveEvaluation eval) {

        Board board = new Board();
        board.loadFromFen(eval.getFenAfter());

        Side attacker = board.getSideToMove();
        Side victim = attacker.flip();

        return hasHangingPiece(board, victim) ||
                hasFork(board, attacker) ||
                hasStrongPin(board, attacker) ||
                isPositionCollapsed(eval);
    }

    private boolean hasHangingPiece(Board board, Side victim) {

        for (Square sq : Square.values()) {
            Piece piece = board.getPiece(sq);
            if (piece == Piece.NONE || piece.getPieceSide() != victim) continue;

            int attackers = Long.bitCount(board.squareAttackedBy(sq, board.getSideToMove()));
            int defenders = Long.bitCount(board.squareAttackedBy(sq, victim));

            if (attackers > defenders && materialValue(piece.getPieceType())  >= 300) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFork(Board board, Side attacker) {

        for (Move move : board.legalMoves()) {
            Board copy = board.clone();
            copy.doMove(move);

            int valuableTargets = 0;

            for (Square sq : Square.values()) {
                Piece p = copy.getPiece(sq);
                if (p == Piece.NONE) continue;
                if (p.getPieceSide() == attacker) continue;
                if (p.getPieceType() == PieceType.KING) continue;

                int value = materialValue(p.getPieceType());
                if (value >= 500 &&
                        copy.isSquareAttackedBy(List.of(sq), attacker)) {
                    valuableTargets++;
                }
            }

            if (valuableTargets >= 2) return true;
        }
        return false;
    }


    private boolean hasStrongPin(Board board, Side attacker) {

        for (Square sq : Square.values()) {
            Piece p = board.getPiece(sq);
            if (p == Piece.NONE) continue;
            if (p.getPieceSide() != attacker.flip()) continue;

            int value = materialValue(p.getPieceType());
            if (value < 300) continue;

            if (board.isSquareAttackedBy(List.of(sq), attacker)) {
                return true;
            }
        }
        return false;
    }


    private boolean isPositionCollapsed(RawMoveEvaluation eval) {
        return eval.getLegalMovesCount() <= 3 &&
                eval.getCentipawnLoss() >= 150;
    }

    private static int materialValue(PieceType type) {
        return switch (type) {
            case PAWN -> 100;
            case KNIGHT, BISHOP -> 300;
            case ROOK -> 500;
            case QUEEN -> 900;
            default -> 0;
        };
    }

}
