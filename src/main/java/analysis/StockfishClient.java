package analysis;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import domain.LichessGame;
import domain.OpeningResponse;
import domain.RawMoveEvaluation;
import domain.enums.GamePhase;
import fetch.LichessOpeningExplorer;
import net.andreinc.neatchess.client.UCI;
import net.andreinc.neatchess.client.model.Analysis;
import parser.PGNParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockfishClient {

    private UCI uci;
    private final LichessOpeningExplorer openingExplorer = new LichessOpeningExplorer();
    private static final int MIN_OPENING_MOVES = 6;
    private static final int MAX_OPENING_MOVES = 16;


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

        if (game == null || game.pgn() == null || game.pgn().isEmpty()) {
            throw new IllegalArgumentException("Invalid game or PGN data.");
        }

        String gameId = game.gameId();
        List<RawMoveEvaluation> evaluations = new ArrayList<>();

        uci.uciNewGame();

        List<String> moves = PGNParser.convertPgnToUciMoves(game.pgn());
        Board board = new Board();
        String openingName = null;
        String openingEco = null;

        for (String uciMove : moves) {

            Side moveColor = board.getSideToMove();
            int moveNumber = board.getMoveCounter();

            String fenBefore = board.getFen();

        /* ==========================
           ENGINE EVAL BEFORE MOVE
           ========================== */
            uci.positionFen(fenBefore);
            Analysis analysisBefore = uci.analysis(depth).getResultOrThrow();

            double evalBest = extractCp(analysisBefore, moveColor, targetColor);

            String bestMoveUci = null;
            if (analysisBefore.getBestMove() != null) {
                bestMoveUci = analysisBefore.getBestMove().getLan();
            }
            int legalMovesCount = board.legalMoves().size();
            boolean forced = legalMovesCount <= 1;

        /* ==========================
           MOVE METADATA
           ========================== */
            Move chessMove = new Move(uciMove, moveColor);

            boolean isCapture = isCapture(board, chessMove);
            boolean isPromotion = isPromotion(chessMove);
            boolean givesCheck = givesCheck(board, chessMove);

            int materialBefore = calculateMaterialBalance(board);

        /* ==========================
           APPLY MOVE
           ========================== */
            board.doMove(chessMove);

            if (moveColor != targetColor) {
                continue;
            }

            String fenAfter = board.getFen();

        /* ==========================
           ENGINE EVAL AFTER MOVE
           ========================== */
            uci.positionFen(fenAfter);
            Analysis analysisAfter = uci.analysis(depth).getResultOrThrow();

            double evalAfter = extractCp(analysisAfter, board.getSideToMove(), targetColor);

        /* ==========================
           MATERIAL & CP LOSS
           ========================== */
            int materialAfter = calculateMaterialBalance(board);
            int materialDelta = materialAfter - materialBefore;

            int relativeMaterial = targetColor == Side.WHITE ? materialAfter : -materialAfter;

            double cpLoss = moveColor == Side.WHITE ? evalBest - evalAfter : evalAfter - evalBest;

            double relativeCpLoss = relativeMaterial != 0 ? cpLoss / Math.abs(relativeMaterial) : cpLoss;

        /* ==========================
           OPENING INFO
           ========================== */
            Optional<OpeningResponse> opening = openingExplorer.getOpeningFromFen(fenBefore);

            boolean inOpeningTheory = opening.isPresent();
            if(openingName == null && opening.isPresent()){
                var openingInfo = opening.get().opening();
                openingName = openingInfo.name();
                openingEco = openingInfo.eco();
            }

        /* ==========================
           GAME PHASE
           ========================== */
            GamePhase phase = inOpeningTheory ? GamePhase.OPENING : determineGamePhase(board, moveNumber);

        /* ==========================
           SAN & MATE THREAT
           ========================== */
            String sanMove = chessMove.toString();  // CORRETTO

            boolean createsMateThreat = Math.abs(evalAfter) > 9000 && Math.abs(evalBest) < 9000;

        /* ==========================
           BUILD EVALUATION
           ========================== */
            RawMoveEvaluation evaluation = new RawMoveEvaluation(
                    gameId,
                    moveColor,
                    moveNumber,
                    uciMove,
                    sanMove,
                    evalBest,
                    evalAfter,
                    evalBest,
                    bestMoveUci,
                    forced,
                    legalMovesCount,
                    relativeMaterial,
                    materialDelta,
                    fenBefore,
                    fenAfter,
                    cpLoss,
                    relativeCpLoss,
                    phase,
                    board.getSideToMove(),
                    isCapture,
                    givesCheck,
                    isPromotion,
                    createsMateThreat,
                    inOpeningTheory,
                    openingName,
                    openingEco
            );

            evaluations.add(evaluation);
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

    public GamePhase determineGamePhase(Board board, int moveNumber) {

        if (isEndgame(board)) {
            return GamePhase.ENDGAME;
        }

        if (moveNumber <= MIN_OPENING_MOVES) {
            return GamePhase.OPENING;
        }

        return GamePhase.MIDDLEGAME;
    }


    private boolean isEndgame(Board board) {
        int majorMinor =
                board.getPieceLocation(Piece.WHITE_QUEEN).size()
                        + board.getPieceLocation(Piece.BLACK_QUEEN).size()
                        + board.getPieceLocation(Piece.WHITE_ROOK).size()
                        + board.getPieceLocation(Piece.BLACK_ROOK).size()
                        + board.getPieceLocation(Piece.WHITE_BISHOP).size()
                        + board.getPieceLocation(Piece.BLACK_BISHOP).size()
                        + board.getPieceLocation(Piece.WHITE_KNIGHT).size()
                        + board.getPieceLocation(Piece.BLACK_KNIGHT).size();

        return majorMinor <= 6;
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

