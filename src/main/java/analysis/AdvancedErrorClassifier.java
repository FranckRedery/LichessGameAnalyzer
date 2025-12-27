package analysis;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import domain.GameError;
import domain.RawMoveEvaluation;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;
import parser.PGNParser;

public class AdvancedErrorClassifier implements ErrorClassifier {

    private static final double MIN_ERROR_CP = 20; // minimo centipawn per considerare un errore
    private static final double INACCURACY_THRESHOLD = 50;
    private static final double MISTAKE_THRESHOLD = 150;
    private static final double BLUNDER_THRESHOLD = 300;

    @Override
    public GameError classify(RawMoveEvaluation rawEval) {

        // Scarta mosse con perdita CP troppo bassa
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

        // 1. Apertura
        if (eval.getPhase() == GamePhase.OPENING) {
            return ErrorCategory.OPENING_KNOWLEDGE;
        }

        // 2. Endgame
        if ((eval.getPhase() == GamePhase.ENDGAME || eval.getPhase() == GamePhase.MIDDLEGAME)
                && Math.abs(eval.getMaterialBalance()) < 1000) {
            return ErrorCategory.ENDGAME_TECHNIQUE;
        }

        // 3. Tattico avanzato
        if (eval.isCapture() || eval.isCheck() || eval.isPromotion() || hasTacticalPattern(eval)) {
            return ErrorCategory.TACTICAL;
        }

        // 4. Distinzione tra strategico e positional
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

    /**
     * Riconosce schemi tattici basici (fork, pin, discovered attack)
     * Possiamo espandere con pattern più avanzati
     */
    private boolean hasTacticalPattern(RawMoveEvaluation eval) {
        Board board = new Board();
        board.loadFromFen(eval.getFenBefore());

        // Ottieni la mossa UCI
        Move move = PGNParser.convertUciToMove(eval.getUciMove(), eval.getPlayerColor());
        board.doMove(move);

        // Fork: se la mossa attacca almeno due pezzi avversari
        if (attacksMultiplePieces(board, move.getTo(), board.getSideToMove().flip())) return true;

        // TODO: aggiungere pin, scoperta, doppio attacco
        return false;
    }

    /**
     * Conta quanti pezzi avversari sono minacciati da un quadrato
     */
    private boolean attacksMultiplePieces(Board board, com.github.bhlangonijr.chesslib.Square from, com.github.bhlangonijr.chesslib.Side opponent) {
        int attacked = 0;

        for (com.github.bhlangonijr.chesslib.Square sq : com.github.bhlangonijr.chesslib.Square.values()) {
            if (board.getPiece(sq).getPieceSide() == opponent) {
                if (board.isMoveLegal(new com.github.bhlangonijr.chesslib.move.Move(from, sq), true)) {
                    attacked++;
                }
            }
        }
        return attacked >= 2;
    }


}
