package analysis;

import com.github.bhlangonijr.chesslib.Board;
import domain.RawMoveEvaluation;
import net.andreinc.neatchess.client.UCI;
import net.andreinc.neatchess.client.model.Analysis;
import net.andreinc.neatchess.client.model.BestMove;
import net.andreinc.neatchess.client.UCIResponse;
import parser.PGNParser;

import java.util.ArrayList;
import java.util.List;

public class StockfishClient {

    private UCI uci;

    public void start() {
        uci = new UCI();
        uci.startStockfish(); // 'stockfish' deve essere nel PATH o percorso assoluto
    }

    public void stop() {
        if (uci != null) {
            uci.close();
        }
    }

    /**
     * Restituisce la miglior mossa in una posizione FEN
     */
    public BestMove getBestMove(String fen, int depth) {
        uci.uciNewGame();
        uci.positionFen(fen);
        return uci.bestMove(depth).getResultOrThrow();
    }

    public List<RawMoveEvaluation> analyzePGN(String pgn, int depth) throws Exception {
        List<RawMoveEvaluation> evaluations = new ArrayList<>();
        uci.uciNewGame();

        // Ottieni mosse UCI dal PGN
        List<String> moves = PGNParser.convertPgnToUciMoves(pgn);

        Board board = new Board(); // posizione iniziale
        int moveNumber = 1;

        for (String move : moves) {
            String fenBeforeMove = board.getFen();
            uci.positionFen(fenBeforeMove);

            // Analizza posizione prima della mossa
            double bestMoveScore = getCentipawnFromAnalysis(uci.analysis(depth));

            // Applica la mossa sulla Board
            board.doMove(move);
            String fenAfterMove = board.getFen();
            uci.positionFen(fenAfterMove);

            // Analizza posizione dopo la mossa
            double actualMoveScore = getCentipawnFromAnalysis(uci.analysis(depth));

            // Centipawn loss
            double cpLoss = bestMoveScore - actualMoveScore;

            evaluations.add(new RawMoveEvaluation(moveNumber, cpLoss));
            moveNumber++;
        }

        return evaluations;
    }

    /**
     * Estrae centipawn score da UCI Analysis
     */
    private double getCentipawnFromAnalysis(UCIResponse<Analysis> analysisList) {
        if (analysisList == null) return 0;

        Analysis analysis = analysisList.getResultOrThrow();

        if (analysis.getBestMove().getStrength().getScore() != null) {
            return analysis.getBestMove().getStrength().getScore(); // centipawn
        }
        return 0;
    }
}

