package analysis;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import domain.LichessGame;
import domain.RawMoveEvaluation;
import domain.enums.GamePhase;
import net.andreinc.neatchess.client.UCI;
import net.andreinc.neatchess.client.model.Analysis;
import parser.PGNParser;

import java.util.ArrayList;
import java.util.List;

public class StockfishClient {

    private UCI uci;

    public void start() {
        uci = new UCI();
        uci.startStockfish(); // stockfish must be in PATH environment variable
    }

    public void stop() {
        if (uci != null) {
            uci.close();
        }
    }

    public List<RawMoveEvaluation> analyzePGN(LichessGame game, int depth, Side targetColor) throws Exception {
        if(game == null || game.getPgn() == null || game.getPgn().isEmpty()) {
            throw new IllegalArgumentException("Invalid game or PGN data.");
        }

        List<RawMoveEvaluation> evaluations = new ArrayList<>();
        uci.uciNewGame();

        List<String> moves = PGNParser.convertPgnToUciMoves(game.getPgn());
        Board board = new Board();

        for (String move : moves) {

            Side moveColor = board.getSideToMove();
            int moveNumber = board.getMoveCounter();
            boolean isTargetMove = moveColor == targetColor;

            String fenBefore = board.getFen();
            uci.positionFen(fenBefore);
            double evalBest = extractCp(uci.analysis(depth).getResultOrThrow(), moveColor, targetColor);

            int legalMoves = board.legalMoves().size();
            boolean isForced = legalMoves <= 1;

            Move chessMove = new Move(move, moveColor);

            boolean isCapture = isCapture(board, chessMove);
            boolean isPromotion = isPromotion(chessMove);
            boolean givesCheck = givesCheck(board, chessMove);

            board.doMove(chessMove);

            if (!isTargetMove) {
                continue;
            }

            String fenAfter = board.getFen();
            uci.positionFen(fenAfter);
            double evalAfter = extractCp(uci.analysis(depth).getResultOrThrow(), moveColor.flip(), targetColor);

            int material = calculateMaterialBalance(board);
            int relativeMaterial = (targetColor == Side.WHITE) ? material : -material;

            double cpLoss = (moveColor == Side.WHITE) ? evalBest - evalAfter : evalAfter - evalBest;

            double relativeCpLoss =
                    (relativeMaterial != 0)
                            ? cpLoss / Math.abs(relativeMaterial)
                            : cpLoss;

            GamePhase phase = determineGamePhase(board);

            if(evalAfter != 0 && evalBest != 0) {
                RawMoveEvaluation eval = new RawMoveEvaluation(
                        moveColor,
                        moveNumber,
                        move,
                        evalBest,
                        evalAfter,
                        isForced,
                        legalMoves,
                        relativeMaterial,
                        fenBefore,
                        fenAfter,
                        cpLoss,
                        relativeCpLoss,
                        phase,
                        isCapture,
                        givesCheck,
                        isPromotion
                );

                evaluations.add(eval);
            }

        }

        return evaluations;
    }


    private double extractCp(Analysis analysis, Side sideToMove, Side pov) {
        if (analysis == null || analysis.getBestMove() == null){
            return 0;
        }

        var strength = analysis.getBestMove().getStrength();
        return strength.getScore() != null ? normalizeEval(strength.getScore() * 100, sideToMove, pov) : 0;
    }

    private double normalizeEval(double eval, Side sideToMove, Side pov) {
        if (sideToMove == pov) {
            return eval;
        }
        return -eval;
    }


    private int calculateMaterialBalance(Board board) {
        return
                100 * (board.getPieceLocation(Piece.WHITE_PAWN).size()
                        - board.getPieceLocation(Piece.BLACK_PAWN).size())
                        + 320 * (board.getPieceLocation(Piece.WHITE_KNIGHT).size()
                        - board.getPieceLocation(Piece.BLACK_KNIGHT).size())
                        + 330 * (board.getPieceLocation(Piece.WHITE_BISHOP).size()
                        - board.getPieceLocation(Piece.BLACK_BISHOP).size())
                        + 500 * (board.getPieceLocation(Piece.WHITE_ROOK).size()
                        - board.getPieceLocation(Piece.BLACK_ROOK).size())
                        + 900 * (board.getPieceLocation(Piece.WHITE_QUEEN).size()
                        - board.getPieceLocation(Piece.BLACK_QUEEN).size());
    }

    public static GamePhase determineGamePhase(Board board) {

        int material =
                9 * (board.getPieceLocation(Piece.WHITE_QUEEN).size()
                        + board.getPieceLocation(Piece.BLACK_QUEEN).size())
                        + 5 * (board.getPieceLocation(Piece.WHITE_ROOK).size()
                        + board.getPieceLocation(Piece.BLACK_ROOK).size())
                        + 3 * (board.getPieceLocation(Piece.WHITE_BISHOP).size()
                        + board.getPieceLocation(Piece.BLACK_BISHOP).size())
                        + 3 * (board.getPieceLocation(Piece.WHITE_KNIGHT).size()
                        + board.getPieceLocation(Piece.BLACK_KNIGHT).size());

        if (material >= 24) return GamePhase.OPENING;
        if (material >= 12) return GamePhase.MIDDLEGAME;
        return GamePhase.ENDGAME;
    }

    private boolean isCapture(Board board, Move move) {
        return board.getPiece(move.getTo()) != Piece.NONE;
    }

    private boolean isPromotion(Move move) {
        return move.getPromotion() != null && move.getPromotion() != Piece.NONE;
    }

    private boolean givesCheck(Board board, Move move) {
        Board copy = board.clone();
        copy.doMove(move);
        return copy.isKingAttacked();
    }


}

